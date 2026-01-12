package gr.hua.dit.StudyRooms.web;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

//Controller για Δοκιμές!!!!!!
@RestController
public class TestController {

    private final PersonRepository personRepository;

    public TestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Controller for <strong> testing </strong>.
     */
    @GetMapping(value = "test/error/404", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        return "error/404";
    }

    @GetMapping(value = "test/error/error")
    public String testError(){
        return "error/error";
    }

    @GetMapping(value = "/test/error/NullPointerException")
    public String testNullPointerException(){
        final Integer a=null;
        final int b=0;
        final int c=a+b; //throws NullPointerException
        return "error/NullPointerException";
    }
}
