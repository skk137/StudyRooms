package gr.hua.dit.StudyRooms.core.security;

import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Service που υλοποιεί το UserDetailsService του Spring Security.
// Χρησιμοποιείται για την ανάκτηση χρηστών κατά το authentication.
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    // Repository πρόσβασης στα δεδομένα των χρηστών
    private final PersonRepository personRepository;

    // Constructor  του PersonRepository
    public ApplicationUserDetailsService(final PersonRepository personRepository){
        if (personRepository == null) throw new NullPointerException();
        this.personRepository = personRepository;
    }

    // Φορτώνει τα στοιχεία χρήστη με βάση το username
    // Το username μπορεί να είναι είτε huaId είτε email
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Έλεγχος για null username
        if (username == null)
            throw new UsernameNotFoundException("Empty username");

        // Αφαίρεση κενών χαρακτήρων από την αρχή και το τέλος
        String u = username.strip();

        // Έλεγχος για κενό username
        if (u.isBlank())
            throw new UsernameNotFoundException("Empty username");

        // Αν περιέχει '@' θεωρείται email, αλλιώς θεωρείτε huaId
        var personOpt = u.contains("@")
                ? personRepository.findByEmailIgnoreCase(u)
                : personRepository.findByHuaIdIgnoreCase(u);

        // Μετατροπή του domain entity Person σε UserDetails
        return personOpt
                .map(person -> new ApplicationUserDetails(
                        person.getId(),
                        person.getHuaId(),        // χρησιμοποιείται σταθερά ως principal
                        person.getPasswordHash(),
                        person.getPersonType()
                ))
                // Αν δεν βρεθεί χρήστης, αποτυγχάνει το authentication
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User " + u + " not found"
                        )
                );
    }
}