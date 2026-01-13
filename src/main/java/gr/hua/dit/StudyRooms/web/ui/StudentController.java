package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.*;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import gr.hua.dit.StudyRooms.core.service.impl.PenaltyServiceImpl;
import gr.hua.dit.StudyRooms.core.service.model.BookingRequest;
import gr.hua.dit.StudyRooms.core.service.model.BookingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



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
    private final PersonRepository personRepository;
    private final PenaltyServiceImpl penaltyServiceImpl;
    private final CurrentUserProvider currentUserProvider;


    public StudentController(RoomService roomService,
                             FavoriteService favoriteService,
                             BookingService bookingService,
                             PersonRepository personRepository,
                             PenaltyServiceImpl penaltyServiceImpl,
                             CurrentUserProvider currentUserProvider) {
        this.roomService = roomService;
        this.favoriteService = favoriteService;
        this.bookingService = bookingService;
        this.personRepository = personRepository;
        this.penaltyServiceImpl = penaltyServiceImpl;
        this.currentUserProvider = currentUserProvider;
    }

    private Person requireStudent() {
        var me = currentUserProvider.getCurrentUser().orElseThrow();
        // Με το SecurityConfig σου, αυτό είναι ήδη student όταν μπαίνεις σε /student/**
        return personRepository.findById(me.id()).orElseThrow();
    }



    @GetMapping("/dashboard")
    public String dashboardStudent(Model model) {

        Person user = requireStudent();

        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.now().withSecond(0).withNano(0);
        LocalTime endTime = startTime.plusHours(1);

        List<Room> rooms = roomService.getAllRooms();
        Set<Long> favoriteRoomIds = favoriteService.getFavoriteRoomIds(user);
        Set<Long> availableRoomIds = bookingService.getAvailableRoomIds(date, startTime, endTime);

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
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {
        Person user = requireStudent();

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


    @GetMapping("/profile")
    public String profile(Model model) {

        Person user = requireStudent();

        model.addAttribute("student", user);
        model.addAttribute("editMode", false);

        return "student-profile";
    }


    @GetMapping("/profile/edit")
    public String editProfile(Model model) {

        Person user = requireStudent();

        model.addAttribute("student", user);
        model.addAttribute("editMode", true);

        return "student-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            Model model
    ) {

        Person user = requireStudent();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);

        personRepository.save(user);

        model.addAttribute("student", user);
        model.addAttribute("editMode", false);
        model.addAttribute("successMessage", "Τα στοιχεία σας ενημερώθηκαν!");

        return "student-profile";
    }

    @GetMapping("/bookings")
    public String studentBookings(Model model) {

        Person user = requireStudent();

        List<Booking> bookings = bookingService.getBookingsByStudent(user);
        model.addAttribute("bookings", bookings);

        return "student-bookings";
    }


    //CreateRoom
    @GetMapping("/bookings/create/{roomId}")
    public String showCreateBookingForm(
            @PathVariable Long roomId,
            Model model
    ) {
        Person student = requireStudent();

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);
        model.addAttribute("today", LocalDate.now());

        return "student-booking-create";
    }

    @PostMapping("/bookings/create/{roomId}")
    public String createBooking(
            @PathVariable Long roomId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {
        Person student = requireStudent();

        if (date == null || startTime == null || endTime == null) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("today", LocalDate.now());
            return "student-booking-create";
        }

        BookingResult result = bookingService.bookRoom(
                new BookingRequest(
                        roomId,
                        student.getId(),
                        date,
                        startTime,
                        endTime
                )
        );

        if (!result.success()) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("errorMessage", result.message());
            model.addAttribute("today", LocalDate.now());
            return "student-booking-create";
        }

        return "redirect:/student/bookings";
    }

    @PostMapping("/bookings/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        Person student = requireStudent();

        BookingResult result = bookingService.cancelBooking(id);

        if (result.success()) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Η κράτηση ακυρώθηκε επιτυχώς"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    result.message()
            );
        }

        return "redirect:/student/bookings";
    }

    @PostMapping("/bookings/checkin/{id}")
    public String checkInBooking(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        Person student = requireStudent();

        BookingResult result = bookingService.checkIn(id);

        if (result.success()) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Επιτυχές check-in!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    result.message()
            );
        }

        return "redirect:/student/bookings";
    }

    //Προβολή Penalties (O φοιτητης δεν μπορεί να μεταβάλλει τα penalties ή να τα διαγράψει, μόνο η γραμματεία)
    @GetMapping("/penalties")
    public String myPenalties(Model model) {

        Person student = requireStudent();

        List<Penalty> penalties = penaltyServiceImpl.getPenaltiesForStudent(student);

        model.addAttribute("penalties", penalties);
        model.addAttribute("today", LocalDate.now());

        return "student-penalties";
    }















}
























