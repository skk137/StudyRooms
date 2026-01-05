package gr.hua.dit.StudyRooms.core.model;


import jakarta.persistence.*;

@Entity
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Person student;  // Φοιτητής

    private int weeks;  // Διάρκεια Ποινής (0 εως 8 Εβδομάδες ) Εαν ειναι 0 η cacncelled θα θεωρείται χωρίς ποινή!

    private boolean canceled; // αν η ποινή έχει ακυρωθεί

    //Empty Constructor
    public Penalty() {}

    public Penalty(Long id, Person student, int weeks, boolean canceled) {
        this.id = id;
        this.student = student;
        this.weeks = weeks;
        this.canceled = canceled;
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
