package gr.hua.dit.StudyRooms.core.repository;

import gr.hua.dit.StudyRooms.core.model.Favorite;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByStudentAndRoom(Person student, Room room);

    List<Favorite> findAllByStudent(Person student);

    boolean existsByStudentAndRoom(Person student, Room room);

    void deleteByStudentAndRoom(Person student, Room room);


}
