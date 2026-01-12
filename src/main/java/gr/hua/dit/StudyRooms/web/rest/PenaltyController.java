package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.service.PenaltyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/literature/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    // Λίστα penalties
    @GetMapping
    public String penaltiesList(Model model, @SessionAttribute("loggedInUser") Person user) {

        //Δεύτερος έλεγχος (1ος στο LogIn Controller) τύπου person ωστε, ακόμα και αν κάποιος προσπαθήσει να έχει Direct URL access, να αποτραπεί.
        if (user.getPersonType() != PersonType.LITERATURE) {
            return "redirect:/login";
        }

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
