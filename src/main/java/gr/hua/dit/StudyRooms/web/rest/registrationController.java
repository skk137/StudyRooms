package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * UI Controller for managing student/literature registration to the system.
 *
 */


@Controller
public class registrationController {

    //Saving in DB, through JPA Repository.
    private final PersonService personService; //debug


    public registrationController(PersonService personService, PersonService personRepository) {
        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
    }



    //Serves the registration form (HTML FILE)
    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {

        //Αρχικοποίηση αδειου person, καθώς θα γεμίσει απο το form.
        model.addAttribute("createPersonRequest", new CreatePersonRequest( PersonType.STUDENT, "", "","","","",""));

        return "register"; //name of thymeleaf
    }


    //handles the registration form Submission (POST HTTP REQUEST)
    @PostMapping("/register")
    public String showRegistrationFormSubmission(
            @ModelAttribute("createPersonRequest") CreatePersonRequest createPersonRequest,
            final Model model)
    {   //Managing post
        final PersonView personView = this.personService.createPerson(createPersonRequest);
        return "redirect:/login"; //redirect στο login μετα το success registeration.

    }

}
