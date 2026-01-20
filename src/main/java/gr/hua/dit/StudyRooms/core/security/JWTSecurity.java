package gr.hua.dit.StudyRooms.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JWTSecurity {

    // Κλειδί για HMAC
    private final SecretKey key;

    // Issuer του token (δείχνει ποίος έχει κάνει την έκδοση)
    private final String issuer;

    // Audience του token (Δείχνει για ποιο σύστημα/πελάτη προορίζεται)
    private final String audience;

    // Χρόνος ζωής token σε λεπτά
    private final long ttlMinutes;

    // Διαβάζει παραμέτρους JWT από το application configuration
    public JWTSecurity(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience,
            @Value("${security.jwt.ttl-minutes:60}") long ttlMinutes
    ) {
        // Έλεγχοι εγκυρότητας ρυθμίσεων στην εκκίνηση της εφαρμογής
        if (secret == null || secret.isBlank())
            throw new IllegalArgumentException("security.jwt.secret is empty");
        if (issuer == null || issuer.isBlank())
            throw new IllegalArgumentException("security.jwt.issuer is empty");
        if (audience == null || audience.isBlank())
            throw new IllegalArgumentException("security.jwt.audience is empty");
        if (ttlMinutes <= 0)
            throw new IllegalArgumentException("security.jwt.ttl-minutes must be > 0");

        // Δημιουργία κλειδιού HMAC από το secret
        // Το secret πρέπει να είναι αρκετά μεγάλο για ασφάλεια (για παράδειγμα μεγαλύτερο απο  32 χαρακτήρες)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Αποθήκευση metadata που θα μπαίνουν και θα ελέγχονται στο token
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    // Εκδίδει νέο JWT για συγκεκριμένο subject και ρόλους
    public String issue(String subject, List<String> roles) {

        // Χρόνος έκδοσης και χρόνος λήξης
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(ttlMinutes));

        // Δημιουργία JWT με standard claims και custom claim "roles"
        return Jwts.builder()
                .issuer(issuer)
                .audience().add(audience).and()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", roles)
                // Υπογραφή token με το HMAC key
                .signWith(key)
                .compact();
    }

    // Κάνει parse και validation ενός JWT και επιστρέφει τα claims του
    public Claims parse(String token) {

        // Επαλήθευση υπογραφής και έλεγχος ότι issuer/audience ταιριάζουν
        // Αν το token είναι άκυρο ή ληγμένο, θα πετάξει exception
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}