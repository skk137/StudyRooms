package gr.hua.dit.StudyRooms.core.repository;


import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomAndDate(Room room, LocalDate date); //debug may not to find by date but only id...
    List<Booking> findByStudent(Person student);



}
