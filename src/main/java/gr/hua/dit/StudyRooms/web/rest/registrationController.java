package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonResult;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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


    //Σερβίρουμε το Registration form (HTML FILE)
    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {

        //Αρχικοποίηση αδειου person, καθώς θα γεμίσει απο το form.
        final CreatePersonRequest createPersonRequest  = new CreatePersonRequest( PersonType.STUDENT, "", "","","","","");
        model.addAttribute("createPersonRequest",createPersonRequest); // Περνάει το Object createPersonRequest, ωστε να γεμιστεί απο το form.
        return "register"; //name of thymeleaf
    }


    //handles the registration form Submission (POST HTTP REQUEST)
    @PostMapping("/register")
    public String showRegistrationFormSubmission(
            @Valid @ModelAttribute("createPersonRequest") CreatePersonRequest createPersonRequest,
            final BindingResult bindingResult,//BindingResult πρέπει να είναι ακριβώς απο κάτω απο το @ModelAttribute.
            final Model model
            )
    {
        //Managing post     ifAuthUtils //to-do
        final CreatePersonResult createPersonResult = this.personService.createPerson(createPersonRequest); //Στέλνουμε τα δεδομέμα για να δημιουργηθεί ο χρήστης.


        if (createPersonResult.created()) {
            return "redirect:/login"; //redirect στο login μετα το success registeration.
        }

        //Μόνο εαν υπάρχουν λάθη στη φόρμα.
        if (bindingResult.hasErrors()) {
            return "register";
        }

        model.addAttribute("createPersonRequest", createPersonRequest); //Pass the same form data, that user types before submit.
        model.addAttribute("errorMessage", createPersonResult.reason()); //Error/reason message.
        return "register";
    }

}
