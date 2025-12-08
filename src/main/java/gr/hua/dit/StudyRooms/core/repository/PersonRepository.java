package gr.hua.dit.StudyRooms.core.repository;


import gr.hua.dit.StudyRooms.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Person} entity.
 */

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {





}
