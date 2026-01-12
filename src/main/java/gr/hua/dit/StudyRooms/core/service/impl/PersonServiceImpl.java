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
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public final class PersonServiceImpl implements PersonService {


    private final Validator validator;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Autowired
    private AuthService authService;

    //Constructor
    public PersonServiceImpl(final Validator validator,final PersonRepository personRepository, final PersonMapper personMapper) {


        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (personMapper == null) throw new NullPointerException("personMapper is null");
        if (validator == null) throw new NullPointerException("validator is null");

        this.validator = validator;
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

        //CreatePersonRequest Validation με βάση τα validation annotations, στην Person.
        final Set<ConstraintViolation<CreatePersonRequest>> requestViolations =
                this.validator.validate(createPersonRequest);
        if (!requestViolations.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final ConstraintViolation<CreatePersonRequest> violation : requestViolations ) {
                sb
                        .append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("\n");

            }
            return CreatePersonResult.fail(sb.toString());
        }

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

        //HuaId Validation.
        if (personRepository.existsByHuaId(huaId)) {
            return CreatePersonResult.fail("Το HUA ID χρησιμοποιείται ήδη");
        }


        //Instantiate person.
        Person person = new Person();
        person.setId(null); // auto generated
        person.setHuaId(huaId);
        person.setPersonType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setPhone(phone);
        person.setPasswordHash(passwordHash);
        person.setCreatedAt(null); // auto-generated με @CreationTimestamp

        // ---------------------------------------------------------------------
        final Set<ConstraintViolation<Person>> personViolations = this.validator.validate(person);
        if (!personViolations.isEmpty()){
            return CreatePersonResult.fail("Validation error");
        }


        authService.createPerson(person);

        //Map person to personview.
        final PersonView personView = this.personMapper.convertPersonToPersonView(person) ;

        return CreatePersonResult.success(personView);
    }

    @Override
    public void updateStudentProfile(
            Person student,
            String firstName,
            String lastName,
            String email,
            String phone
    ){
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);

        personRepository.save(student);
    }

}


