package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.security.CurrentUser;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "gr.hua.dit.StudyRooms.web")
public class AuthenticatedControllersAdvice {

    private final CurrentUserProvider currentUserProvider;

    public AuthenticatedControllersAdvice(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @ModelAttribute("me")
    public CurrentUser addCurrentUserAsMe() {

        return this.currentUserProvider.getCurrentUser().orElse(null);
    }
}


