package gr.hua.dit.StudyRooms.core.security;

import java.util.Optional;

// Interface που ορίζει τη λειτουργία αυθεντικοποίησης API clients
// Χρησιμοποιείται για service-to-service authentication
public interface ClientDetailsService {

    // Ελέγχει τα credentials ενός API client (id και secret)
    Optional<ClientDetails> authenticate(String id, String secret);
}