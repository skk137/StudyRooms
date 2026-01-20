package gr.hua.dit.StudyRooms.external.notifications;

// DTO που αναπαριστά αίτημα αποστολής SMS προς εξωτερική υπηρεσία ειδοποιήσεων
public record NocSendSmsRequest(

        // Αριθμός τηλεφώνου σε μορφή E.164 για παράδειγμα  +3069XXXXXXXX
        String e164,

        // Περιεχόμενο του SMS που θα αποσταλεί
        String content

) {}