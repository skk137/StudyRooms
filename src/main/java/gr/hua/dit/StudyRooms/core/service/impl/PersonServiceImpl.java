package gr.hua.dit.StudyRooms.core.service.impl;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.AuthService;
import gr.hua.dit.StudyRooms.core.service.Mapper.PersonMapper;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonResult;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class PersonServiceImpl implements PersonService {

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Autowired
    private AuthService authService;

    //Constructor
    public PersonServiceImpl(final PasswordEncoder passwordEncoder,
                             PersonRepository personRepository,
                             final PersonMapper personMapper) {
        if (passwordEncoder == null) throw new NullPointerException("passwordEncodes is null");
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (personMapper == null) throw new NullPointerException("personMapper is null");

        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }


    @Override
    public List<PersonView> getPeople() {
        return List.of();
    }


    @Override
    public CreatePersonResult createPerson(CreatePersonRequest createPersonRequest) {
        if (createPersonRequest==null) throw new NullPointerException("createPersonRequest==null");

        final PersonType type = createPersonRequest.PersonType();
        final String huaId = createPersonRequest.huaId().strip(); //removing whitespaces
        final String firstName = createPersonRequest.FirstName().strip();
        final String lastName = createPersonRequest.LastName().strip();
        final String email = createPersonRequest.Email().strip();
        final String phone = createPersonRequest.Phone().strip();
        final String passwordHash = createPersonRequest.passwordHash().strip();



        // Email address validation.
        if (!email.toLowerCase().endsWith("@hua.gr")) {
            return CreatePersonResult.fail("Μόνο emails με @hua.gr επιτρέπονται");
            //throw new IllegalArgumentException("Μόνο emails με @hua.gr επιτρέπονται");
        }


        if (this.personRepository.existsByHuaIdIgnoreCase(huaId)) {
            return CreatePersonResult.fail("HUA ID already registered");
        }


        final String hashedPassword = this.passwordEncoder.encode(passwordHash);


        // Instantiate person.

        Person person = new Person();
        person.setId(null); // auto generated
        person.setHuaId(huaId);
        person.setPersonType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setPhone(phone);
        person.setPasswordHash(hashedPassword);
        person.setCreatedAt(null); // auto-generated με @CreationTimestamp


        // Save/Insert to DB.
        // debug person=this.personRepository.save(person);

        authService.createPerson(person);

        //Map person to personview.
        final PersonView personView = this.personMapper.convertPersonToPersonView(person) ; //to-do







        return CreatePersonResult.success(personView);
    }
}
