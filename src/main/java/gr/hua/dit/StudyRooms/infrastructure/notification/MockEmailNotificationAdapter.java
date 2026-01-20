package gr.hua.dit.StudyRooms.infrastructure.notification;

import gr.hua.dit.StudyRooms.core.port.EmailNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Mock adapter για αποστολή ειδοποιήσεων
// Χρησιμοποιείται σε περιβάλλον development ή testing
// Δεν πραγματοποιεί πραγματική αποστολή SMS
@Component
public class MockEmailNotificationAdapter implements EmailNotificationPort {

    // Logger για καταγραφή των mock ειδοποιήσεων
    private static final Logger log =
            LoggerFactory.getLogger(MockEmailNotificationAdapter.class);

    // Προσομοίωση αποστολής SMS μέσω απλής καταγραφής στο log
    @Override
    public void sendSms(String phoneNumber, String message) {

        // Καταγραφή στοιχείων ειδοποίησης αντί για πραγματική αποστολή
        log.info("MOCK SMS");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
    }
}