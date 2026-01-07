package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/student")
public class StudentController {

    private final RoomService roomService;
    private final FavoriteService favoriteService;
    private final BookingService bookingService;

    public StudentController(RoomService roomService,
                             FavoriteService favoriteService,
                             BookingService bookingService) {
        this.roomService = roomService;
        this.favoriteService = favoriteService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboardStudent(
            @SessionAttribute("loggedInUser") Person user,
            Model model
    ) {

        // Security check
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        // Default χρονικό διάστημα (προβολή)
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.now().withSecond(0).withNano(0);
        LocalTime endTime = startTime.plusHours(1);

        // Data
        List<Room> rooms = roomService.getAllRooms();
        Set<Long> favoriteRoomIds = favoriteService.getFavoriteRoomIds(user);
        Set<Long> availableRoomIds =
                bookingService.getAvailableRoomIds(date, startTime, endTime);

        // Model
        model.addAttribute("rooms", rooms);
        model.addAttribute("favoriteRoomIds", favoriteRoomIds);
        model.addAttribute("availableRoomIds", availableRoomIds);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "student-dashboard";
    }


    @GetMapping("/favorites")
    public String favoriteRooms(
            @SessionAttribute("loggedInUser") Person user,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        if (date == null) date = LocalDate.now();
        if (startTime == null) startTime = LocalTime.of(10, 0);
        if (endTime == null) endTime = LocalTime.of(12, 0);

        List<Room> favoriteRooms = favoriteService.getFavoriteRooms(user);
        Set<Long> favoriteRoomIds = favoriteService.getFavoriteRoomIds(user);
        Set<Long> availableRoomIds =
                bookingService.getAvailableRoomIds(date, startTime, endTime);

        model.addAttribute("favoriteRooms", favoriteRooms);
        model.addAttribute("favoriteRoomIds", favoriteRoomIds);
        model.addAttribute("availableRoomIds", availableRoomIds);

        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "student-favorites";
    }


}