package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Penalty;
import gr.hua.dit.StudyRooms.core.repository.PenaltyRepository;
import gr.hua.dit.StudyRooms.core.service.PenaltyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;

    public PenaltyServiceImpl(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    public List<Penalty> getAllPenalties() {
        return penaltyRepository.findAll();
    }

    @Override
    public Penalty cancelPenalty(Long id) { //Το θεωρούμε completed.
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty not found"));
        penalty.setCanceled(true);
        return penaltyRepository.save(penalty);
    }

    @Override
    public Penalty reducePenalty(Long id) {
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty not found"));
        if (penalty.getWeeks() > 1) {
            penalty.setWeeks(penalty.getWeeks() - 1); //Εαν ειναι λιγότερο απο 2 weeks δεν πέρνει αλλο reduce μονο cancel.
        }
        return penaltyRepository.save(penalty);
    }
}
