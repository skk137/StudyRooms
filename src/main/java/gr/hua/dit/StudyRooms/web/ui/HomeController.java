package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;


@Controller
public class HomeController {


    private final RoomService roomService;

    public HomeController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping("/")
    public String home(Model model){ //Δεν ελέγχουμε για PersonType καθώς, θέλουμε να το βλέπουν ακόμα και ανώνυμοι χρήστες(όλοι).

        List<Room> rooms = roomService.getAllRooms();

        model.addAttribute("rooms", rooms);

        return "homepage";
    }

}