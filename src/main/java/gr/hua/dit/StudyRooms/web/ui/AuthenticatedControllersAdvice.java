package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.security.CurrentUser;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// ControllerAdvice που εφαρμόζεται σε όλα τα controllers του web layer
// Χρησιμοποιείται για την προσθήκη κοινών δεδομένων στο model των views
@ControllerAdvice(basePackages = "gr.hua.dit.StudyRooms.web")
public class AuthenticatedControllersAdvice {

    // Provider που ανακτά τον τρέχοντα αυθεντικοποιημένο χρήστη
    private final CurrentUserProvider currentUserProvider;

    // Constructor  του CurrentUserProvider
    public AuthenticatedControllersAdvice(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    // Προσθέτει τον τρέχοντα χρήστη στο model με το όνομα "me"
    // Το attribute είναι διαθέσιμο σε όλα τα UI views
    @ModelAttribute("me")
    public CurrentUser addCurrentUserAsMe() {

        // Αν δεν υπάρχει αυθεντικοποιημένος χρήστης, επιστρέφεται null
        return this.currentUserProvider.getCurrentUser().orElse(null);
    }
}