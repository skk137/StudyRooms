package gr.hua.dit.StudyRooms.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * API Integration Client entity (StudyRooms).
 *
 * Used for service-to-service authentication (JWT).
 */
@Entity
@Table(
        name = "api_client",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_api_client_name", columnNames = "name")
        }
)
public class ApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String secret;

    /**
     * CSV list of roles WITHOUT ROLE_ prefix
     * Example: INTEGRATION_READ,INTEGRATION_WRITE
     */
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?!.*\\bROLE_)[A-Z]+_[A-Z]+(?:,[A-Z]+_[A-Z]+)*$")
    @Column(name = "roles_csv", nullable = false, length = 255)
    private String rolesCsv;

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