package gr.hua.dit.StudyRooms.core.repository;

import gr.hua.dit.StudyRooms.core.model.Penalty;
import gr.hua.dit.StudyRooms.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    List<Penalty> findAllByStudent(Person student);


}