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

/**
 * REST controller for API authentication (integration client -> JWT token).
 */
@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientAuthResource {

    private final ClientDetailsService clientDetailsService;
    private final JWTSecurity jwtService;

    public ClientAuthResource(ClientDetailsService clientDetailsService, JWTSecurity jwtService) {
        if (clientDetailsService == null) throw new NullPointerException("clientDetailsService is null");
        if (jwtService == null) throw new NullPointerException("jwtService is null");
        this.clientDetailsService = clientDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/v1/auth/login
     * Request:  { "clientId": "...", "clientSecret": "..." }
     * Response: { "accessToken": "...", "tokenType": "Bearer", "expiresIn": 3600 }
     */
    @PostMapping("/login")
    public ClientTokenResponse login(@RequestBody @Valid ClientTokenRequest request) {

        String clientId = request.clientId();
        String clientSecret = request.clientSecret();

        // Step 1: authenticate integration client
        ClientDetails client = clientDetailsService.authenticate(clientId, clientSecret).orElse(null);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client credentials");
        }

        // Step 2: issue JWT
        String token = jwtService.issue("client:" + client.id(), client.roles().stream().toList());

        return new ClientTokenResponse(token, "Bearer", 60 * 60);
    }
}