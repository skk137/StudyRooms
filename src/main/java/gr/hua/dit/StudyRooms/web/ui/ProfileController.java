package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final CurrentUserProvider currentUserProvider;

    public ProfileController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/profile")
    public String profile() {
        var user = currentUserProvider.getCurrentUser()
                .orElseThrow(); // αν δεν είναι logged in, Spring Security θα τον στείλει /login

        if (user.type() == PersonType.LITERATURE) {
            return "redirect:/literature/dashboard";
        }
        return "redirect:/student/dashboard";
    }
}
