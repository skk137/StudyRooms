package gr.hua.dit.StudyRooms.web.ui;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

// Utility κλάση με βοηθητικές μεθόδους σχετικές με το authentication
// Χρησιμοποιείται κυρίως στο UI layer
public final class AuthUtils {

    // Private constructor για αποτροπή δημιουργίας αντικειμένων
    private AuthUtils() {}

    // Ελέγχει αν ο χρήστης είναι αυθεντικοποιημένος
    public static boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    // Ελέγχει αν ο χρήστης είναι ανώνυμος
    public static boolean isAnonymous(Authentication authentication) {
        return authentication == null
                || authentication instanceof AnonymousAuthenticationToken;
    }
}