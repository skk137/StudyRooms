package gr.hua.dit.StudyRooms.external.notifications;

import gr.hua.dit.StudyRooms.core.port.EmailNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

// Adapter που υλοποιεί το Notification Port μέσω εξωτερικής υπηρεσίας (NOC)
// Αποστέλλει SMS χρησιμοποιώντας REST API
@Primary
@Component
public class NocSmsNotificationAdapter implements EmailNotificationPort {

    // Client για HTTP κλήσεις προς το εξωτερικό σύστημα
    private final RestClient restClient;

    // Βασικό URL της εξωτερικής υπηρεσίας ειδοποιήσεων
    private final String baseUrl;

    // Token αυθεντικοποίησης για την εξωτερική υπηρεσία
    private final String token;

    // Constructor των ρυθμίσεων και του RestClient
    public NocSmsNotificationAdapter(
            RestClient.Builder builder,
            @Value("${external.noc.base-url}") String baseUrl,
            @Value("${external.noc.token}") String token
    ) {
        this.restClient = builder.build();
        this.baseUrl = baseUrl;
        this.token = token;
    }

    // Αποστολή SMS μέσω του εξωτερικού notification service
    @Override
    public void sendSms(String phoneNumber, String message) {

        // Δημιουργία payload του αιτήματος
        Map<String, Object> payload = Map.of(
                "e164", phoneNumber,
                "content", message
        );

        // Εκτέλεση POST request προς το εξωτερικό API
        restClient.post()
                .uri(baseUrl + "/api/v1/sms")
                .contentType(MediaType.APPLICATION_JSON)
                // Authorization header για αυθεντικοποίηση στο εξωτερικό σύστημα
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}