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

/**
 * JWT authentication filter for StudyRooms REST API.
 *
 * - Applies only to /api/v1/**
 * - Skips /api/v1/auth/** (login endpoint)
 * - Reads Authorization: Bearer <token>
 * - Parses JWT, extracts "roles" claim, sets Authentication in SecurityContext
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTSecurity jwtService;

    public JwtAuthenticationFilter(JWTSecurity jwtService) {
        if (jwtService == null) throw new NullPointerException("jwtService is null");
        this.jwtService = jwtService;
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
            {"timestamp":"%s","status":401,"error":"invalid_token","message":"%s"}
            """.formatted(Instant.now(), message.replace("\"", "'")));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // apply only to /api/v1/**
        if (!path.startsWith("/api/v1")) return true;

        // don't filter auth endpoints (so login can be called without token)
        if (path.startsWith("/api/v1/auth/")) return true;

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // No header or not Bearer -> continue (will end up 401 by Security if endpoint requires auth)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            writeError(response, "Empty bearer token");
            return;
        }

        try {
            Claims claims = jwtService.parse(token);

            String subject = claims.getSubject();

            Object rolesObj = claims.get("roles");
            Collection<String> roles = null;

            if (rolesObj instanceof Collection<?> c) {
                roles = c.stream().map(String::valueOf).toList();
            }

            // ✅ FIX: make both branches return List<GrantedAuthority>
            List<GrantedAuthority> authorities =
                    roles == null
                            ? List.<GrantedAuthority>of()
                            : roles.stream()
                            .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                            .toList();

            var authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            LOGGER.warn("JWT auth failed: {}", ex.getMessage());
            writeError(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}