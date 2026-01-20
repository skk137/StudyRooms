package gr.hua.dit.StudyRooms.core.model;

import jakarta.persistence.*;

@Entity
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Person student; //Σε ποιοον student ανήκει

    @ManyToOne(optional = false)
    private Room room;     //Το δωμάτιο, που δηλώνεται, ως αγαπημένο.

    //Empty Constructor
    public Favorite() {
    }
    //Constructor
    public Favorite(Long id, Person student, Room room) {
        this.id = id;
        this.student = student;
        this.room = room;
    }
    //Constructor
    public Favorite(Person student, Room room) {
        this.student = student;
        this.room = room;
    }

    //Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getStudent() {
        return student;
    }

    public void setStudent(Person student) {
        this.student = student;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}


