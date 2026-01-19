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

    private final SecretKey key;
    private final String issuer;
    private final String audience;
    private final long ttlMinutes;

    public JWTSecurity(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience,
            @Value("${security.jwt.ttl-minutes:60}") long ttlMinutes
    ) {
        if (secret == null || secret.isBlank()) throw new IllegalArgumentException("security.jwt.secret is empty");
        if (issuer == null || issuer.isBlank()) throw new IllegalArgumentException("security.jwt.issuer is empty");
        if (audience == null || audience.isBlank()) throw new IllegalArgumentException("security.jwt.audience is empty");
        if (ttlMinutes <= 0) throw new IllegalArgumentException("security.jwt.ttl-minutes must be > 0");

        // IMPORTANT: secret must be sufficiently long for HS256 (>= 32 chars recommended)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    public String issue(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(ttlMinutes));

        return Jwts.builder()
                .issuer(issuer)
                .audience().add(audience).and()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", roles)
                .signWith(key) // JJWT 0.13 chooses HS* based on key type/size
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}