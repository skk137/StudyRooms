package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller που χρησιμοποιείται αποκλειστικά για δοκιμές και debugging
// Βοηθά στον έλεγχο του global error handling και των error templates
@RestController
public class TestController {

    // Repository το οποίο διατηρείται για μελλοντικές δοκιμές αν χρειαστεί
    private final PersonRepository personRepository;

    // Constructor με dependency
    public TestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Endpoint για δοκιμή σελίδας 404
    @GetMapping(value = "/test/error/404", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test404() {
        return "error/404";
    }

    // Endpoint για δοκιμή γενικού error template
    @GetMapping(value = "/test/error/error")
    public String testError(){
        return "error/error";
    }

    // Endpoint που προκαλεί σκόπιμα NullPointerException
    // Χρησιμοποιείται για έλεγχο του GlobalErrorHandlerControllerAdvice
    @GetMapping(value = "/test/error/NullPointerException")
    public String testNullPointerException(){
        final Integer a = null;
        final int b = 0;
        final int c = a + b; // προκαλεί NullPointerException
        return "error/NullPointerException";
    }
}