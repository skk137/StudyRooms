package gr.hua.dit.StudyRooms.web.rest.model;

import gr.hua.dit.StudyRooms.web.rest.ClientAuthResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO που αναπαριστά αίτημα αυθεντικοποίησης API client
// Χρησιμοποιείται από το ClientAuthResource για έκδοση JWT
public record ClientTokenRequest(

        // Αναγνωριστικό του API client
        @NotNull
        @NotBlank
        String clientId,

        // Μυστικό κλειδί του API client
        @NotNull
        @NotBlank
        String clientSecret

) {}