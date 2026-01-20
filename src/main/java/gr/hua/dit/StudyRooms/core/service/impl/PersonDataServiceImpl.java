package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.PersonDataService;
import gr.hua.dit.StudyRooms.core.service.Mapper.PersonMapper;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;

import org.springframework.stereotype.Service;

import java.util.List;

// Υλοποίηση του service που διαχειρίζεται τα δεδομένα των χρηστών (Person)
@Service
public class PersonDataServiceImpl implements PersonDataService {

    // Repository για πρόσβαση στη βάση δεδομένων
    private final PersonRepository personRepository;

    // Mapper για μετατροπή Entity -> View model
    private final PersonMapper personMapper;

    // Constructor με dependency injection
    public PersonDataServiceImpl(final PersonRepository personRepository,
                                 final PersonMapper personMapper) {
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    // Επιστρέφει όλους τους χρήστες του συστήματος σε μορφή PersonView
    @Override
    public List<PersonView> getAllPeople() {

        // Ανάκτηση όλων των χρηστών από τη βάση δεδομένων
        final List<Person> personList = this.personRepository.findAll();

        // Μετατροπή των Person entities σε PersonView objects
        final List<PersonView> personViewList = personList
                .stream()
                .map(this.personMapper::convertPersonToPersonView)
                .toList();

        // Επιστροφή της λίστας
        return personViewList;
    }
}