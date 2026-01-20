package gr.hua.dit.StudyRooms.web.ui;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// UI controller που διαχειρίζεται τη διαδικασία login και logout χρηστών
// Χρησιμοποιείται από το web interface της εφαρμογής
@Controller
public class AuthController {

    // Endpoint για τη σελίδα login
    @GetMapping("/login")
    public String login(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model
    ) {

        // Αν ο χρήστης είναι ήδη αυθεντικοποιημένος,
        // γίνεται ανακατεύθυνση στη σελίδα προφίλ
        if (isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // Το Spring Security προσθέτει παραμέτρους ?error ή ?logout στο URL
        // Εμφανίζονται αντίστοιχα φιλικά μηνύματα στο UI
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid email or password.");
        }

        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        // Επιστροφή του view "login"
        return "login";
    }

    // Ελέγχει αν ο χρήστης είναι αυθεντικοποιημένος
    public static boolean isAuthenticated(final Authentication auth) {
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

    // Ελέγχει αν ο χρήστης είναι ανώνυμος
    public static boolean isAnonymous(final Authentication auth) {
        return auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken;
    }
}