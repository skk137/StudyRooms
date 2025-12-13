package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.service.model.LogInRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class loginController {

        @GetMapping("/login")
        public String showLoginForm(final Model model) {
            model.addAttribute("loginRequest", new LogInRequest("", ""));
            return "login";
        }

        @PostMapping("/login")
        public String handleLogin(
                @ModelAttribute("loginRequest") LogInRequest loginRequest,
                Model model) {

            // προσωρινό
            System.out.println("Login attempt: " + loginRequest.huaId());
            return "error"; // προσωρινό, μετά θα βάλεις auth
        }


}


