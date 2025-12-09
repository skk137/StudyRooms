package gr.hua.dit.StudyRooms.core.service.impl;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(final PersonRepository personRepository) {
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        this.personRepository = personRepository;
    }


    @Override
    public List<PersonView> getPeople() {
        return List.of();
    }

    @Override
    public PersonView createPerson(CreatePersonRequest createPersonRequest) {
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
            throw new IllegalArgumentException("Μόνο emails με @hua.gr επιτρέπονται");
        }


        // Advanced mobile phone number validation.
        // --------------------------------------------------
/*
        final PhoneNumberValidationResult phoneNumberValidationResult
                = this.phoneNumberPort.validate(mobilePhoneNumber);
        if (!phoneNumberValidationResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneNumberValidationResult.e164();

        // --------------------------------------------------

        if (this.personRepository.existsByHuaIdIgnoreCase(huaId)) {
            return CreatePersonResult.fail("HUA ID already registered");
        }

        if (this.personRepository.existsByEmailAddressIgnoreCase(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // --------------------------------------------------

        final PersonType personType_lookup = this.lookupPort.lookup(huaId).orElse(null);
        if (personType_lookup == null) {
            return CreatePersonResult.fail("Invalid HUA ID");
        }
        if (personType_lookup != type) {
            return CreatePersonResult.fail("The provided person type does not match the actual one");
        }

        // --------------------------------------------------
*/
//      final String hashedPassword = this.passwordEncoder.encode(rawPassword);


        // Instantiate person.

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


        // Save/Insert to DB.
        person=this.personRepository.save(person);

        //Map person to personview.
        final PersonView personViewview = null ; //to-do







        return null;
    }
}
