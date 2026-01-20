package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class registrationController {

    private final PersonService personService;

    public registrationController(PersonService personService) {
        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Authentication authentication, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        CreatePersonRequest createPersonRequest =
                new CreatePersonRequest(PersonType.STUDENT, "", "", "", "", "", "");

        model.addAttribute("createPersonRequest", createPersonRequest);
        return "register";
    }

    //Εγγραφή Χρήστη
    @PostMapping("/register")
    public String submit(Authentication authentication,
                         @ModelAttribute("createPersonRequest") CreatePersonRequest createPersonRequest,
                         Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        CreatePersonResult result = this.personService.createPerson(createPersonRequest);

        if (result.created()) {
            return "redirect:/login";
        }

        model.addAttribute("createPersonRequest", createPersonRequest);
        model.addAttribute("errorMessage", result.reason());
        return "register";
    }
}





