package gr.hua.dit.StudyRooms.core.security;

import java.util.Optional;

/**
 * Service for authenticating REST API integration client.
 */
public interface ClientDetailsService {

    Optional<ClientDetails> authenticate(String id, String secret);
}