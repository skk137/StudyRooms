package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Person student;  // Φοιτητής

    private int weeks;  // Διάρκεια Ποινής (0 εως 8 Εβδομάδες ) Εαν ειναι 0 η cacncelled θα θεωρείται χωρίς ποινή!

    private boolean canceled; // αν η ποινή έχει ακυρωθεί

    private LocalDate startDate;
    private LocalDate endDate;

    //Empty Constructor
    public Penalty() {}

    public Penalty(Long id, Person student, int weeks, boolean canceled, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.student = student;
        this.weeks = weeks;
        this.canceled = canceled;
        this.startDate = startDate;
        this.endDate =endDate;
    }

    public long getRemainingDays() {
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

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

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

}
