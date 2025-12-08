package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Person Entity.
 */

@Entity
@Table(name="person", uniqueConstraints = {}, indexes = {})
public class Person {

    // ORM ANNOTATIONS ΩΣΤΕ ΝΑ ΣΧΗΜΑΤΙΣΤΕΙ Η ΒΑΣΗ ΔΟΜΙΚΑ ΟΡΘΑ.
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "hua_id", nullable = false, length = 30)
    private String huaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private PersonType PersonType; // PERSON STUDENT OR LITERATURE

    @Column(name = "first_name", nullable = false, length = 150)
    private String FirstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String LastName;

    @Column(name = "email", nullable = false, length = 255)
    private String Email;

    @Column(name = "phone", nullable = false)
    private String Phone;

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
