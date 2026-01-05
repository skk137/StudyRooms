package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.service.PenaltyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/literature/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    // Λίστα penalties
    @GetMapping
    public String penaltiesList(Model model) {
        model.addAttribute("penalties", penaltyService.getAllPenalties());
        return "literature-penalties";
    }

    // Cancel ποινή
    @PostMapping("/cancel/{id}")
    public String cancelPenalty(@PathVariable Long id) {
        penaltyService.cancelPenalty(id);
        return "redirect:/literature/penalties";
    }

    // Reduce ποινή κατά 1 εβδομάδα
    @PostMapping("/reduce/{id}")
    public String reducePenalty(@PathVariable Long id) {
        penaltyService.reducePenalty(id);
        return "redirect:/literature/penalties";
    }
}
