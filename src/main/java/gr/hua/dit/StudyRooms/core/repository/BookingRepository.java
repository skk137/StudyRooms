package gr.hua.dit.StudyRooms.core.repository;


import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomAndDate(Room room, LocalDate date);
    List<Booking> findByStudent(Person student);


    @Query("""
        SELECT b FROM Booking b
        WHERE b.canceled = false
          AND b.date = :date
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    List<Booking> findActiveBookingsForTimeSlot( //Ωστε να  βρίσκουμε να υπάρχουν θέσεις στο συγκεκριμένο timeslot
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
