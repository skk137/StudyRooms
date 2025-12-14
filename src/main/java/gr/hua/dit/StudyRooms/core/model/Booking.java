package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Person student;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean canceled;

    //Empty Constructor
    public Booking() {}


    public Booking(Long id, Room room, Person student,
                   LocalDate date, LocalTime startTime,
                   LocalTime endTime, boolean canceled) {
        this.id = id;
        this.room = room;
        this.student = student;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.canceled = canceled;
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