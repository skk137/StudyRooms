package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/student/favorites")
public class StudentFavoriteController {

    private final FavoriteService favoriteService;


    public StudentFavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    @PostMapping("/toggle/{roomId}")
    public String toggleFavorite(@PathVariable Long roomId,
                                 @SessionAttribute("loggedInUser") Person student) {

        favoriteService.toggleFavorite(student, roomId);
        return "redirect:/student/dashboard";
    }

}

