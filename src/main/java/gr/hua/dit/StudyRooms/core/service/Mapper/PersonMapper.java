package gr.hua.dit.StudyRooms.core.service.Mapper;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;
import org.springframework.stereotype.Component;

// Mapper κλάση που μετατρέπει το domain entity Person σε PersonView
// Χρησιμοποιείται για την επιστροφή στοιχείων χρήστη προς το API layer
@Component
public class PersonMapper {

    // Μετατρέπει ένα αντικείμενο Person σε PersonView
    // Αν το Person είναι null, επιστρέφεται null
    public PersonView convertPersonToPersonView(final Person person) {
        if (person == null) {
            return null;
        }

        // Δημιουργία PersonView με βασικά στοιχεία του χρήστη
        final PersonView personView = new PersonView(
                person.getId(),
                person.getHuaId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getPhone(),
                person.getPersonType()
        );

        return personView;
    }
}