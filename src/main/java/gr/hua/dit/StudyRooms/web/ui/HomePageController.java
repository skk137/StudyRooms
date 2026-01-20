package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;


@Controller
public class HomePageController {

    private final RoomService roomService;

    public HomePageController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {

        // Αν ο χρήστης είναι logged in (όχι anonymous)
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            // STUDENT --> student dashboard
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
                return "redirect:/student/dashboard";
            }

            // LITERATURE --> literature dashboard
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LITERATURE"))) {
                return "redirect:/literature/dashboard";
            }
        }

        // Ανώνυμος χρήστης → homepage με rooms
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);

        return "homepage";
    }

}







