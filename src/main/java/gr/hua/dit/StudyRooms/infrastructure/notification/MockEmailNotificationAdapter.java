package gr.hua.dit.StudyRooms.infrastructure.notification;

import gr.hua.dit.StudyRooms.core.port.EmailNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockEmailNotificationAdapter implements EmailNotificationPort {

    private static final Logger log =
            LoggerFactory.getLogger(MockEmailNotificationAdapter.class);

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("📱 MOCK SMS");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
    }
}