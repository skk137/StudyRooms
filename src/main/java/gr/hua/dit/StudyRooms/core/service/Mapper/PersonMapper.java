package gr.hua.dit.StudyRooms.core.service.Mapper;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import org.springframework.stereotype.Component;

// Mapper to Connect Person with PersonView
@Component
public class PersonMapper {

    public PersonView convertPersonToPersonView(final Person person) {
        if (person == null) {
            return null;
        }
        final PersonView personView = new PersonView( person.getId(),
                person.getHuaId(), person.getFirstName(),
                person.getLastName(),person.getEmail(),person.getPhone(), person.getPersonType()
        );



        return personView;
    }



}
