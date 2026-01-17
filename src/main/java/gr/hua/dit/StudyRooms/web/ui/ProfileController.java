package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.security.ApplicationUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(Authentication authentication) {

        // Αν δεν είναι authenticated, πάμε στο login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Παίρνουμε τον logged-in user
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof ApplicationUserDetails user)) {
            return "redirect:/login";
        }

        // Redirect ανάλογα με τον ρόλο
        PersonType role = user.getPersonType();
        if (role == null) {
            return "redirect:/login";
        }

        return switch (role) {
            case STUDENT -> "redirect:/student/dashboard";
            case LITERATURE -> "redirect:/literature/dashboard";
            default -> "redirect:/login";
        };
    }
}
