package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.model.Penalty;
import gr.hua.dit.StudyRooms.core.model.Person;

import java.util.List;



public interface PenaltyService {

    List<Penalty> getAllPenalties();
    Penalty cancelPenalty(Long id);
    Penalty reducePenalty(Long id); // μειώνει κατά 1 εβδομάδα
    List<Penalty> getPenaltiesForStudent(Person student);
    boolean hasActivePenalty(Person student);

}





