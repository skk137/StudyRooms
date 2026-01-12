package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Person Entity.
 */

@Entity
@Table(name="person", uniqueConstraints = {}, indexes = {})
public class Person {

    //ORM ANNOTATIONS, για τον ορθό σχηαματισμό της δομής της βάσης.
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Column(name = "hua_id", nullable = false, length = 30, unique = true)
    private String huaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private PersonType PersonType; // PERSON STUDENT OR LITERATURE

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 150)
    private String FirstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 150)
    private String LastName;


    @NotBlank
    @Size(max = 155)
    @jakarta.validation.constraints.Email
    @Column(name = "email", nullable = false, length = 255)
    private String Email;

    @NotNull
    @NotBlank
    @Size(max = 25)
    @Column(name = "phone", nullable = false)
    private String Phone;

    @NotNull
    @NotBlank
    @Size(max=255)
    @Column(name = "password_Hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    //Empty Constructor
    public Person() { };

    public Person(Long id, String huaId,
                  PersonType personType, String firstName,
                  String lastName, String email, String phone,
                  String passwordHash, Instant createdAt) {
        this.id = id;
        this.huaId = huaId;
        PersonType = personType;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHuaId() {
        return huaId;
    }

    public void setHuaId(String huaId) {
        this.huaId = huaId;
    }

    public PersonType getPersonType() {
        return PersonType;
    }

    public void setPersonType(PersonType personType) {
        PersonType = personType;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString(){
        return "Person{" + this.id + ", " + this.huaId + ", " + this.PersonType +'}';
    }







}
