package gr.hua.dit.StudyRooms.core.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

// Filter αυθεντικοποίησης JWT για το REST API.
// Διαβάζει το Authorization header (Bearer token), κάνει parse το JWT,
// Authentication στο SecurityContext.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Logger για καταγραφή σφαλμάτων αυθεντικοποίησης
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Service που αναλαμβάνει τη δημιουργία/επαλήθευση/ανάλυση JWT
    private final JWTSecurity jwtService;

    // Constructor του jwtService
    public JwtAuthenticationFilter(JWTSecurity jwtService) {
        if (jwtService == null) throw new NullPointerException("jwtService is null");
        this.jwtService = jwtService;
    }

    // Βοηθητική μέθοδος για επιστροφή 401 με JSON μήνυμα σφάλματος
    private void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Επιστρέφουμε structured error για να είναι χρήσιμο σε client/SPA
        response.getWriter().write("""
            {"timestamp":"%s","status":401,"error":"invalid_token","message":"%s"}
            """.formatted(Instant.now(), message.replace("\"", "'")));
    }

    // Δηλώνει πότε δεν πρέπει να εφαρμοστεί το φίλτρο
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Το φίλτρο εφαρμόζεται μόνο στα endpoints του REST API
        if (!path.startsWith("/api/v1")) return true;

        // Τα endpoints /api/v1/auth/** (π.χ. login) πρέπει να είναι προσβάσιμα χωρίς token
        if (path.startsWith("/api/v1/auth/")) return true;

        return false;
    }

    // Λογική φίλτρου που εκτελείται μία φορά ανά request
    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Ανάκτηση Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // Αν δεν υπάρχει Bearer token, συνεχίζουμε το chain
        // (αν το endpoint απαιτεί auth, θα απορριφθεί αργότερα από το Security)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Εξαγωγή token από το header
        String token = authorizationHeader.substring("Bearer ".length()).trim();

        // Άδειο token θεωρείται μη έγκυρο
        if (token.isBlank()) {
            writeError(response, "Empty bearer token");
            return;
        }

        try {
            // Parse/validation του JWT (υπογραφή, λήξη κ.λπ.)
            Claims claims = jwtService.parse(token);

            // Subject του token (συνήθως username / userId)
            String subject = claims.getSubject();

            // Ανάκτηση ρόλων από claim "roles"
            Object rolesObj = claims.get("roles");
            Collection<String> roles = null;

            // Μετατροπή του claim σε συλλογή από Strings
            if (rolesObj instanceof Collection<?> c) {
                roles = c.stream().map(String::valueOf).toList();
            }

            // Μετατροπή ρόλων σε GrantedAuthority για χρήση από Spring Security
            // Οι ρόλοι στο Spring Security ακολουθούν τη σύμβαση "ROLE_<ROLE>"
            List<GrantedAuthority> authorities =
                    roles == null
                            ? List.<GrantedAuthority>of()
                            : roles.stream()
                            .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                            .toList();

            // Δημιουργία Authentication object και τοποθέτησή του στο SecurityContext
            var authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // Σε αποτυχία (invalid signature / expired token / malformed token)
            LOGGER.warn("JWT auth failed: {}", ex.getMessage());
            writeError(response, "Invalid or expired token");
            return;
        }

        // Συνέχιση του filter chain προς controller
        filterChain.doFilter(request, response);
    }
}