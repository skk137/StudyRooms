package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.service.PersonDataService;
import gr.hua.dit.StudyRooms.core.service.model.PersonView;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller για ανάκτηση στοιχείων χρηστών
// Εκθέτει endpoints κάτω από το /api/v1/person
@RestController
@RequestMapping(
        value = "/api/v1/person",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PersonResource {

    // Service που περιέχει τη business logic για τα άτομα
    private final PersonDataService personDataService;

    // Constructor  του PersonDataService
    public PersonResource(final PersonDataService personDataService) {
        if (personDataService == null) throw new NullPointerException();
        this.personDataService = personDataService;
    }

    // Endpoint για ανάκτηση όλων των χρηστών
    // Προσβάσιμο μόνο από clients με ρόλο INTEGRATION_READ
    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<PersonView> people() {

        // Επιστρέφει λίστα χρηστών σε μορφή PersonView
        return this.personDataService.getAllPeople();
    }
}