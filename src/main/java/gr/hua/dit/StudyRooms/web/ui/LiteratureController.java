package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
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
    private final CurrentUserProvider currentUserProvider;
    private final PersonRepository personRepository;
    //Constructor
    public LiteratureController(RoomService roomService,
                                CurrentUserProvider currentUserProvider,
                                PersonRepository personRepository) {
        this.roomService = roomService;
        this.currentUserProvider = currentUserProvider;
        this.personRepository = personRepository;
    }

    private Person requireLiterature() {
        var me = currentUserProvider.getCurrentUser().orElseThrow();
        return personRepository.findById(me.id()).orElseThrow();
    }


    @GetMapping("/literature/dashboard")
    public String dashboardLiterature(Model model) {

        Person user = requireLiterature();

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
        Person user = requireLiterature();
        RoomRequest roomRequest = new RoomRequest(name, capacity, openTime, closeTime);
        roomService.createRoom(roomRequest);
        return "redirect:/literature/dashboard";
    }

    @PostMapping("/literature/rooms/delete/{id}")
    public String deleteRoomPost(@PathVariable Long id) {
        Person user = requireLiterature();
        roomService.deleteRoom(id);
        return "redirect:/literature/dashboard";
    }


    //Edit Feature
    @GetMapping("/literature/rooms/edit/{id}")
    public String editRoom(
            @PathVariable Long id,
            Model model) {
        Person user = requireLiterature();

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