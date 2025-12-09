package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
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
    private final PersonRepository personRepository;
    public registrationController(PersonRepository personRepository) {
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        this.personRepository = personRepository;
    }


    //Serves the registration form (HTML FILE)
    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {

        //Αρχικοποίηση αδειου person, καθώς θα γεμίσει απο το form.
        model.addAttribute("person", new Person(null,"", PersonType.STUDENT, "", "","","","",null));

        return "register"; //name of thymeleaf
    }


    //handles the registration form Submission (POST HTTP REQUEST)
    @PostMapping("/register")
    public String showRegistrationFormSubmission(
            @ModelAttribute("person") Person person,
            final Model model)
    {   //Managing post
        System.out.println(person.toString()); //PRE SAVE
        person=this.personRepository.save(person);
        System.out.println(person.toString()); //POST SAVE
        model.addAttribute("person", person);
        return "redirect:/login"; //redirect στο login μετα το success registeration.

    }

}
