package gr.hua.dit.StudyRooms.external.notifications;

import gr.hua.dit.StudyRooms.core.port.EmailNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Primary
@Component
public class NocSmsNotificationAdapter implements EmailNotificationPort {

    private final RestClient restClient;
    private final String baseUrl;
    private final String token;

    public NocSmsNotificationAdapter(
            RestClient.Builder builder,
            @Value("${external.noc.base-url}") String baseUrl,
            @Value("${external.noc.token}") String token
    ) {
        this.restClient = builder.build();
        this.baseUrl = baseUrl;
        this.token = token;
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        Map<String, Object> payload = Map.of(
                "e164", phoneNumber,
                "content", message
        );

        restClient.post()
                .uri(baseUrl + "/api/v1/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)   // ✅ secured external call
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}