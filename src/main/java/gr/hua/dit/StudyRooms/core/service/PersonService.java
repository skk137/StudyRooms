package gr.hua.dit.StudyRooms.core.service;


import gr.hua.dit.StudyRooms.core.service.model.CreatePersonRequest;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;

import java.util.List;

//for managing persons(students/literature)
public interface PersonService {


    List<PersonView> getPeople();

    PersonView createPerson(CreatePersonRequest createPersonRequest);







}
