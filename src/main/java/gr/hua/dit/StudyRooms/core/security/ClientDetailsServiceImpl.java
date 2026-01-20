package gr.hua.dit.StudyRooms.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

// Υλοποίηση του ClientDetailsService
// Παρέχει μηχανισμό authentication για API integration client
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    // Αναμενόμενο username του integration client
    // Φορτώνεται από το application configuration
    private final String expectedUsername;

    // Αναμενόμενο password του integration client
    // Φορτώνεται από το application configuration
    private final String expectedPassword;

    // Constructor των credentials από το application.properties / yaml
    public ClientDetailsServiceImpl(
            @Value("${api.integration.username}") String expectedUsername,
            @Value("${api.integration.password}") String expectedPassword
    ) {
        // Έλεγχος εγκυρότητας των ρυθμίσεων κατά την εκκίνηση της εφαρμογής
        if (expectedUsername == null || expectedUsername.isBlank())
            throw new IllegalArgumentException("api.integration.username is empty");
        if (expectedPassword == null || expectedPassword.isBlank())
            throw new IllegalArgumentException("api.integration.password is empty");

        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    // Έλεγχος ταυτότητας API client με βάση id και secret
    @Override
    public Optional<ClientDetails> authenticate(String id, String secret) {

        // Έλεγχος για null τιμές
        if (id == null) throw new NullPointerException("id is null");
        if (secret == null) throw new NullPointerException("secret is null");

        // Αφαίρεση περιττών κενών χαρακτήρων
        String u = id.strip();
        String p = secret.strip();

        // Απόρριψη κενών credentials
        if (u.isBlank() || p.isBlank())
            return Optional.empty();

        // Έλεγχος αντιστοίχισης με τα αναμενόμενα credentials
        if (expectedUsername.equals(u) && expectedPassword.equals(p)) {

            // Επιστροφή στοιχείων client χωρίς το πραγματικό secret
            return Optional.of(new ClientDetails(
                    u,
                    "*****",      // το secret δεν επιστρέφεται για λόγους ασφάλειας
                    Set.of("INTEGRATION_READ")
            ));
        }

        // Αποτυχία authentication
        return Optional.empty();
    }
}