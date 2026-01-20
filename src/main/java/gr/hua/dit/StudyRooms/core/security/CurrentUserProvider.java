package gr.hua.dit.StudyRooms.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Component που παρέχει πρόσβαση στον τρέχοντα αυθεντικοποιημένο χρήστη
// Απομονώνει τη χρήση του SecurityContext από την υπόλοιπη εφαρμογή
@Component
public final class CurrentUserProvider {

    // Επιστρέφει τα βασικά στοιχεία του τρέχοντος χρήστη, αν υπάρχει authentication
    public Optional<CurrentUser> getCurrentUser() {

        // Ανάκτηση του Authentication από το SecurityContext
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Αν δεν υπάρχει authentication, δεν υπάρχει συνδεδεμένος χρήστης
        if (authentication == null) {
            return Optional.empty();
        }

        // Ανάκτηση του principal από το Authentication
        Object principal = authentication.getPrincipal();

        // Έλεγχος αν ο principal είναι ApplicationUserDetails
        if (principal instanceof ApplicationUserDetails userDetails) {

            // Δημιουργία lightweight αντικειμένου CurrentUser
            return Optional.of(new CurrentUser(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getPersonType()
            ));
        }

        // Σε κάθε άλλη περίπτωση δεν επιστρέφεται χρήστης
        return Optional.empty();
    }
}