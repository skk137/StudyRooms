package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.security.ApplicationUserDetails;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import gr.hua.dit.StudyRooms.core.service.impl.PersonServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Controller
@RequestMapping("/student/favorites")
public class StudentFavoriteController {

    private final FavoriteService favoriteService;
    private final PersonServiceImpl personServiceImpl;


    public StudentFavoriteController(FavoriteService favoriteService, PersonServiceImpl personServiceImpl) {
        this.favoriteService = favoriteService;
        this.personServiceImpl = personServiceImpl;
    }

    //Endpoint για προσθήκη / αφαίρεση αγαπημένου δωματίου
    //Το roomId περνάει από το URL
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/toggle/{roomId}")
    public String toggleFavorite(
            @PathVariable Long roomId, //Πέρνουμε το id του δωματίου απο το path.
            @AuthenticationPrincipal ApplicationUserDetails userDetails){

        //Πέρνουμε id logged in χρήστη.
        Long personId = userDetails.getId();

        Person student = personServiceImpl.findById(personId)
                .orElseThrow(() -> new IllegalStateException("Logged-in student not found"));

        favoriteService.toggleFavorite(student, roomId);

        //redirect πίσω στο dashboard
        return "redirect:/student/dashboard";
    }

}

