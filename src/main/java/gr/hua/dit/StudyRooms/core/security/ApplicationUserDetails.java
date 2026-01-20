package gr.hua.dit.StudyRooms.core.security;

import gr.hua.dit.StudyRooms.core.model.PersonType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Υλοποίηση του Spring Security {@link UserDetails}.
 *
 * Σκοπός:
 * - Αναπαριστά τον αυθεντικοποιημένο χρήστη της εφαρμογής.
 * - Χρησιμοποιείται από το Spring Security κατά τη διαδικασία
 *   authentication και authorization.
 *
 * Σημείωση:
 * - Η κλάση λειτουργεί ως adapter μεταξύ του domain model (Person)
 *   και του μηχανισμού ασφάλειας του Spring.
 */
public class ApplicationUserDetails implements UserDetails {

    /**
     * Μοναδικό αναγνωριστικό χρήστη στο σύστημα.
     */
    private final Long id;

    /**
     * Username του χρήστη.
     *
     * Μπορεί να είναι:
     * - email
     * - ακαδημαϊκό ID (HUA id)
     */
    private final String username;

    /**
     * Κρυπτογραφημένο password (hash).
     *
     * Δεν αποθηκεύεται ποτέ plaintext κωδικός.
     */
    private final String passwordHash;

    /**
     * Τύπος χρήστη του συστήματος (π.χ. STUDENT, ADMIN κ.λπ.).
     *
     * Χρησιμοποιείται για τον καθορισμό ρόλων/δικαιωμάτων.
     */
    private final PersonType personType;

    /**
     * Constructor για τη δημιουργία αντικειμένου UserDetails
     * από το αντίστοιχο domain entity.
     */
    public ApplicationUserDetails(
            Long id,
            String username,
            String passwordHash,
            PersonType personType
    ) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.personType = personType;
    }

    /**
     * Επιστρέφει το μοναδικό ID του χρήστη.
     *
     * Χρήσιμο για audit/logging και business logic.
     */
    public Long getId() {
        return id;
    }

    /**
     * Επιστρέφει τον τύπο χρήστη (ρόλο σε επίπεδο domain).
     */
    public PersonType getPersonType() {
        return personType;
    }

    /**
     * Μετατρέπει τον {@link PersonType} σε Spring Security authority.
     *
     * Σύμβαση Spring Security:
     * - Οι ρόλοι πρέπει να έχουν πρόθεμα "ROLE_".
     *
     * Παράδειγμα:
     * - PersonType.STUDENT → ROLE_STUDENT
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + personType.name())
        );
    }


    //Επιστρέφει το password hash του χρήστη.
    @Override
    public String getPassword() {
        return passwordHash;
    }

    //Επιστρέφει το username που χρησιμοποιείται στο authentication.

    @Override
    public String getUsername() {
        return username;
    }


     //Δηλώνει αν ο λογαριασμός έχει λήξει
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    //Δηλώνει αν ο λογαριασμός είναι κλειδωμένος
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


     //Δηλώνει αν τα credentials (password) έχουν λήξει.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

     //Δηλώνει αν ο λογαριασμός είναι ενεργός
    @Override
    public boolean isEnabled() {
        return true;
    }
}