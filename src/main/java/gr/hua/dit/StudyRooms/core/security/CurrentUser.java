package gr.hua.dit.StudyRooms.core.security;

import gr.hua.dit.StudyRooms.core.model.PersonType;

// Απλό immutable αντικείμενο που αναπαριστά τον τρέχοντα αυθεντικοποιημένο χρήστη
// Χρησιμοποιείται κυρίως για μεταφορά βασικών στοιχείων χρήστη
public record CurrentUser(

        // Μοναδικό αναγνωριστικό χρήστη
        long id,

        // Username του χρήστη (π.χ. huaId ή email)
        String username,

        // Τύπος χρήστη του συστήματος (ρόλος σε επίπεδο domain)
        PersonType type

) {}