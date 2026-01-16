package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import gr.hua.dit.StudyRooms.core.service.model.RoomRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;


@Controller
public class LiteratureController {

    private final RoomService roomService;
    //Constructor
    public LiteratureController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/literature/dashboard")
    public String dashboardLiterature(@SessionAttribute("loggedInUser") Person user, Model model) {


        //Δεύτερος έλεγχος (1ος στο LogIn Controller) τύπου person ωστε, ακόμα και αν κάποιος προσπαθήσει να έχει Direct URL access, να αποτραπεί.
        if (user.getPersonType() != PersonType.LITERATURE) {
            return "redirect:/login";
        }
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("roomRequest", new RoomRequest());
        model.addAttribute("roomId", null);
        return "literature-dashboard";
    }


//    @GetMapping("/rooms/new")
//    public String newRoomForm(Model model) {
//        model.addAttribute("roomRequest", new RoomRequest());
//        return "new-room-form"; //HTML για form
//    }

    @PostMapping("/literature/rooms")
    public String createRoom(
            @RequestParam String name,
            @RequestParam int capacity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime openTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closeTime
    ) {
        RoomRequest roomRequest = new RoomRequest(name, capacity, openTime, closeTime);
        roomService.createRoom(roomRequest);
        return "redirect:/literature/dashboard";
    }

    @PostMapping("/rooms/delete/{id}")
    public String deleteRoomPost(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return "redirect:/literature/dashboard";
    }


    //Edit Feature
    @GetMapping("/literature/rooms/edit/{id}")
    public String editRoom(
            @SessionAttribute("loggedInUser") Person user,
            @PathVariable Long id,
            Model model) {

        //Δεύτερος έλεγχος (1ος στο LogIn Controller) τύπου person ωστε, ακόμα και αν κάποιος προσπαθήσει να έχει Direct URL access, να αποτραπεί.
        if (user.getPersonType() != PersonType.LITERATURE) {
            return "redirect:/login";
        }

        Room room = roomService.getRoomById(id);

        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("roomRequest",
                new RoomRequest(
                        room.getName(),
                        room.getCapacity(),
                        room.getOpenTime(),
                        room.getCloseTime()
                ));
        model.addAttribute("roomId", id);

        return "literature-dashboard";
    }

    // Ενημέρωση room (Edit)
    @PostMapping("/literature/rooms/{id}")
    public String updateRoom(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam int capacity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime openTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closeTime
    ) {
        RoomRequest request = new RoomRequest(name, capacity, openTime, closeTime);
        roomService.updateRoom(id, request);

        return "redirect:/literature/dashboard";
    }

}