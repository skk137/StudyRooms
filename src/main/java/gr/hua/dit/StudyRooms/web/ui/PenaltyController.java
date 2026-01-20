package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.service.PenaltyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Controller για τη διαχείριση ποινών από το προσωπικό βιβλιοθήκης
@Controller
@RequestMapping("/literature/penalties")
public class PenaltyController {

    // Service που χειρίζεται τη λογική των ποινών
    private final PenaltyService penaltyService;

    // Constructor με dependency injection
    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    // Προβολή λίστας ποινών
    @GetMapping
    public String penaltiesList(Model model) {


        // Προσθήκη λίστας ποινών στο model για εμφάνιση στο UI
        model.addAttribute("penalties", penaltyService.getAllPenalties());

        // Επιστροφή του αντίστοιχου template
        return "literature-penalties";
    }

    // Ακύρωση ποινής
    @PostMapping("/cancel/{id}")
    public String cancelPenalty(@PathVariable Long id) {

        // Κλήση service για ακύρωση της ποινής
        penaltyService.cancelPenalty(id);

        // Redirect στη λίστα ποινών
        return "redirect:/literature/penalties";
    }

    // Μείωση διάρκειας ποινής κατά μία εβδομάδα
    @PostMapping("/reduce/{id}")
    public String reducePenalty(@PathVariable Long id) {

        // Κλήση service για μείωση της ποινής
        penaltyService.reducePenalty(id);

        // Redirect στη λίστα ποινών
        return "redirect:/literature/penalties";
    }
}