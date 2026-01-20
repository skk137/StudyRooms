package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.security.ClientDetails;
import gr.hua.dit.StudyRooms.core.security.ClientDetailsService;
import gr.hua.dit.StudyRooms.core.security.JWTSecurity;
import gr.hua.dit.StudyRooms.web.rest.model.ClientTokenRequest;
import gr.hua.dit.StudyRooms.web.rest.model.ClientTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// REST controller για αυθεντικοποίηση API integration clients
// Εκδίδει JWT tokens που χρησιμοποιούνται για πρόσβαση στο REST API
@RestController
@RequestMapping(
        value = "/api/v1/auth",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClientAuthResource {

    // Service που ελέγχει τα credentials του integration client
    private final ClientDetailsService clientDetailsService;

    // Service για έκδοση και επαλήθευση JWT
    private final JWTSecurity jwtService;

    // Constructor  services
    public ClientAuthResource(
            ClientDetailsService clientDetailsService,
            JWTSecurity jwtService
    ) {
        if (clientDetailsService == null)
            throw new NullPointerException("clientDetailsService is null");
        if (jwtService == null)
            throw new NullPointerException("jwtService is null");

        this.clientDetailsService = clientDetailsService;
        this.jwtService = jwtService;
    }

    // Endpoint αυθεντικοποίησης API client
    // Δέχεται clientId και clientSecret και επιστρέφει JWT
    @PostMapping("/login")
    public ClientTokenResponse login(
            @RequestBody @Valid ClientTokenRequest request
    ) {

        // Ανάκτηση στοιχείων client από το request
        String clientId = request.clientId();
        String clientSecret = request.clientSecret();

        // Έλεγχος ταυτότητας integration client
        ClientDetails client =
                clientDetailsService.authenticate(clientId, clientSecret)
                        .orElse(null);

        // Αν αποτύχει το authentication, επιστρέφεται 401
        if (client == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid client credentials"
            );
        }

        // Έκδοση JWT με βάση το client id και τους ρόλους του
        String token = jwtService.issue(
                "client:" + client.id(),
                client.roles().stream().toList()
        );

        // Επιστροφή token response
        return new ClientTokenResponse(
                token,
                "Bearer",
                60 * 60
        );
    }
}