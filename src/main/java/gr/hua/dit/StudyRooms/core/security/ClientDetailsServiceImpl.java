package gr.hua.dit.StudyRooms.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private final String expectedUsername;
    private final String expectedPassword;

    public ClientDetailsServiceImpl(
            @Value("${api.integration.username}") String expectedUsername,
            @Value("${api.integration.password}") String expectedPassword
    ) {
        if (expectedUsername == null || expectedUsername.isBlank())
            throw new IllegalArgumentException("api.integration.username is empty");
        if (expectedPassword == null || expectedPassword.isBlank())
            throw new IllegalArgumentException("api.integration.password is empty");

        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    @Override
    public Optional<ClientDetails> authenticate(String id, String secret) {
        if (id == null) throw new NullPointerException("id is null");
        if (secret == null) throw new NullPointerException("secret is null");

        String u = id.strip();
        String p = secret.strip();

        if (u.isBlank() || p.isBlank()) return Optional.empty();

        if (expectedUsername.equals(u) && expectedPassword.equals(p)) {
            return Optional.of(new ClientDetails(
                    u,
                    "*****", // δεν χρειάζεται να επιστρέφουμε το secret
                    Set.of("INTEGRATION_READ")
            ));
        }

        return Optional.empty();
    }
}