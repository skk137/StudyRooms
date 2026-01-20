package gr.hua.dit.StudyRooms.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Entity που μοντελοποιεί έναν "API Client" του συστήματος StudyRooms.
 * Σκοπός :
 * Ένας εξωτερικός/εσωτερικός client, να πιστοποιείται με (name, secret)
 *   και λαμβάνει JWT με βάση τα δικαιώματα (roles) που του έχουν αποδοθεί.
 */
@Entity
@Table(
        name = "api_client",
        uniqueConstraints = {
                /**
                 * Βάζουμε CONSTRAINT στο "name" ώστε να μην υπάρχουν 2 clients
                 * με ίδιο αναγνωριστικό.
                 */
                @UniqueConstraint(name = "uk_api_client_name", columnNames = "name")
        }
)
public class ApiClient {

    /**
     * Πρωτεύον κλειδί
     * Δημιουργείται αυτόματα από τη βάση.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Αναγνωριστικό ονόματος του API client (π.χ. "integration").
    @NotNull
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Μυστικό (shared secret) του API client.
     * Χρήση:
     * Χρησιμοποιείται κατά την αυθεντικοποίηση (login) του client.
     */
    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String secret;

    //Σύνολο ρόλων/δικαιωμάτων του client σε μορφή CSV (comma-separated values).
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?!.*\\bROLE_)[A-Z]+_[A-Z]+(?:,[A-Z]+_[A-Z]+)*$")
    @Column(name = "roles_csv", nullable = false, length = 255)
    private String rolesCsv;

    //Constructor
    public ApiClient() {}

    public ApiClient(Long id, String name, String secret, String rolesCsv) {
        this.id = id;
        this.name = name;
        this.secret = secret;
        this.rolesCsv = rolesCsv;
    }


    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSecret() { return secret; }
    public String getRolesCsv() { return rolesCsv; }


    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSecret(String secret) { this.secret = secret; }
    public void setRolesCsv(String rolesCsv) { this.rolesCsv = rolesCsv; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiClient that)) return false;
        return Objects.equals(name, that.name)
                && Objects.equals(secret, that.secret)
                && Objects.equals(rolesCsv, that.rolesCsv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, secret, rolesCsv);
    }

}