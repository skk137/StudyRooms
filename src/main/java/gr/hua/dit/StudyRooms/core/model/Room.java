package gr.hua.dit.StudyRooms.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalTime;

@Entity
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int capacity;

    private LocalTime openTime;
    private LocalTime closeTime;

    //Empty Constructor
    public Room() {}

    //Constructor
    public Room(String name, int capacity, LocalTime openTime, LocalTime closeTime) {
        this.name = name;
        this.capacity = capacity;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    //Getters/Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }


}
