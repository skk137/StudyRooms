package gr.hua.dit.StudyRooms.core.service;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.CreatePersonResult;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;

import java.util.List;
import java.util.Optional;

//for managing persons(students/literature)
public interface PersonService {


    List<PersonView> getPeople();

    CreatePersonResult createPerson(CreatePersonRequest createPersonRequest);

    void updateStudentProfile(
            Person student,
            String firstName,
            String lastName,
            String email,
            String phone
    );


    Optional<Person> findById(Long id);




}
