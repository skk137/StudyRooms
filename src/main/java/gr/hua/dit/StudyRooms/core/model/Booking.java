package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Booking {


    //@GeneratedValue
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Person student;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean canceled;
    private boolean checkedin;

    //Empty Constructor
    public Booking() {}


    public Booking(Long id, Room room, Person student,
                   LocalDate date, LocalTime startTime,
                   LocalTime endTime, boolean canceled, boolean checkedin) {
        this.id = id;
        this.room = room;
        this.student = student;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.canceled = canceled;
        this.checkedin = checkedin;
    }


    public boolean isCheckedin() {
        return checkedin;
    }

    public void setCheckedin(boolean checkedin) {
        this.checkedin = checkedin;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}