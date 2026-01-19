package gr.hua.dit.StudyRooms.core.repository;

import gr.hua.dit.StudyRooms.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByHuaId(String huaId);

    boolean existsByHuaId(String huaId);

    Optional<Person> findByHuaIdIgnoreCase(String huaId);

    Optional<Person> findByEmailIgnoreCase(String email);
}