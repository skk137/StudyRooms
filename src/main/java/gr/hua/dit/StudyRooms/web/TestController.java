package gr.hua.dit.StudyRooms.web;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TestController {



    private final PersonRepository personRepository;

    public TestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    /**
     * Rest EndPoint for testing.
     */
    @GetMapping(value = "test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        Person person = new Person();
        person.setId(null); //auto gen
        person.setHuaId("it2022095");
        person.setPersonType(PersonType.LITERATURE);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("skk");
        person.setPhone("6989159045");
        person.setPasswordHash("137");
        person.setCreatedAt(Instant.now()); //just now

        person = this.personRepository.save(person);


        return person.toString();
    }




}
