package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.service.AuthService;
import gr.hua.dit.StudyRooms.core.service.model.LogInRequest;
import gr.hua.dit.StudyRooms.core.service.model.LoginResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class loginController {

    private final AuthService authService;

    public loginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LogInRequest("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LogInRequest loginRequest,
                              Model model,
                              HttpSession session) {

        LoginResult result = authService.login(loginRequest);

        //Αν υπάρχει λάθος στο logIn, εμφανίζουμε errorMessage.
        if (!result.success()) {
            model.addAttribute("errorMessage", result.reason());
            return "login";
        }

        //Στο user έχουμε ολη την εγγραφή του χρήστη.
        Person user = result.person();
        session.setAttribute("loggedInUser", user);

        //Ώστε να τον πετάει στο κατάλληλο dashboard.
        if (user.getPersonType() == PersonType.LITERATURE) {
            return "redirect:/literature/dashboard";
        } else if (user.getPersonType() == PersonType.STUDENT) {
            return "redirect:/student/dashboard";
        } else {
            return "redirect:/login";
        }
    }

}


