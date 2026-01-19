package gr.hua.dit.StudyRooms.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Column(name = "hua_id", nullable = false, length = 30, unique = true)
    private String huaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private PersonType personType;

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @NotBlank
    @Size(max = 155)
    @Email
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotNull
    @NotBlank
    @Size(max = 25)
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Person() {}

    public Person(Long id, String huaId, PersonType personType, String firstName,
                  String lastName, String email, String phone,
                  String passwordHash, Instant createdAt) {
        this.id = id;
        this.huaId = huaId;
        this.personType = personType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getHuaId() { return huaId; }
    public PersonType getPersonType() { return personType; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setHuaId(String huaId) { this.huaId = huaId; }
    public void setPersonType(PersonType personType) { this.personType = personType; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Person{" + id + ", " + huaId + ", " + personType + '}';
    }
}