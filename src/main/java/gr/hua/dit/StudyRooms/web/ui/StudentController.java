package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.*;
import gr.hua.dit.StudyRooms.core.security.ApplicationUserDetails;
import gr.hua.dit.StudyRooms.core.security.CurrentUserProvider;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import gr.hua.dit.StudyRooms.core.service.PersonService;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import gr.hua.dit.StudyRooms.core.service.impl.PenaltyServiceImpl;
import gr.hua.dit.StudyRooms.core.service.model.BookingRequest;
import gr.hua.dit.StudyRooms.core.service.model.BookingResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final PersonService personService;
    private final PenaltyServiceImpl penaltyService;
    private final CurrentUserProvider currentUserProvider;

    public StudentController(RoomService roomService,
                             FavoriteService favoriteService,
                             BookingService bookingService,
                             PersonService personService,
                             PenaltyServiceImpl penaltyService,
                             CurrentUserProvider currentUserProvider) {
        this.roomService = roomService;
        this.favoriteService = favoriteService;
        this.bookingService = bookingService;
        this.personService = personService;
        this.penaltyService = penaltyService;
        this.currentUserProvider = currentUserProvider;
    }

    /* =========================================================
       Helpers
       ========================================================= */

    private Person requireStudent() {
        var me = currentUserProvider.getCurrentUser().orElseThrow();
        return personService.findById(me.id()).orElseThrow();
    }

    /* =========================================================
       Dashboard
       ========================================================= */

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Person student = requireStudent();

        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.now().withSecond(0).withNano(0);
        LocalTime end = start.plusHours(1);

        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("favoriteRoomIds", favoriteService.getFavoriteRoomIds(student));
        model.addAttribute("availableRoomIds",
                bookingService.getAvailableRoomIds(date, start, end));

        model.addAttribute("date", date);
        model.addAttribute("startTime", start);
        model.addAttribute("endTime", end);

        return "student-dashboard";
    }

    /* =========================================================
       Favorites
       ========================================================= */

    @GetMapping("/favorites")
    public String favorites(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {

        Person student = requireStudent();

        //if (date == null) date = LocalDate.now();
        //if (startTime == null) startTime = LocalTime.of(10, 0);
        //if (endTime == null) endTime = LocalTime.of(12, 0);

        List<Room> favoriteRooms = favoriteService.getFavoriteRooms(student);
        Set<Long> favoriteRoomIds = favoriteService.getFavoriteRoomIds(student);
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

    /* =========================================================
       Profile
       ========================================================= */

    @GetMapping("/profile")
    public String profile(Model model) {

        Person student = requireStudent();

        model.addAttribute("student", student);
        model.addAttribute("editMode", false);

        return "student-profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {

        Person student = requireStudent();

        model.addAttribute("student", student);
        model.addAttribute("editMode", true);

        return "student-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @AuthenticationPrincipal ApplicationUserDetails userDetails,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            Model model
    ){

        Long personId = userDetails.getId();

        Person student = personService.findById(personId)
                .orElseThrow(() -> new IllegalStateException("Logged-in student not found"));

        personService.updateStudentProfile(
                student,
                firstName,
                lastName,
                email,
                phone
        );

        model.addAttribute("student", student);
        model.addAttribute("editMode", false);
        model.addAttribute("successMessage", "Τα στοιχεία σάς ενημερώθηκαν!!! ");

        //return "redirect:/student/profile";
        return "student-profile";
    }

    /* =========================================================
       Bookings
       ========================================================= */

    @GetMapping("/bookings")
    public String bookings(Model model) {

        Person student = requireStudent();

        bookingService.checkAndApplyPenalties(student);

        //Όλες οι κρατήσεις για τον φοιτητή
        List<Booking> bookings = bookingService.getBookingsByStudent(student);

        model.addAttribute("bookings", bookings);
        return "student-bookings";
    }

    @GetMapping("/bookings/create/{roomId}")
    public String createBookingForm(
            @PathVariable Long roomId,
            Model model
    ) {
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
                new BookingRequest(roomId, student.getId(), date, startTime, endTime)
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
    public String checkIn(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        BookingResult result = bookingService.checkIn(id);

        if (result.success()) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Επιτυχές check-in !"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    result.message()
            );
        }

        return "redirect:/student/bookings";
    }

    /* =========================================================
       Penalties
       ========================================================= */

    //Προβολή Penalties (O φοιτητης δεν μπορεί να μεταβάλλει τα penalties ή να τα διαγράψει, μόνο η γραμματεία)
    @GetMapping("/penalties")
    public String penalties(Model model) {

        Person student = requireStudent();

        List<Penalty> penalties = penaltyService.getPenaltiesForStudent(student); //debug penaltyServiceImpl

        model.addAttribute("penalties", penalties);
        model.addAttribute("today", LocalDate.now());

        return "student-penalties";

    }

}


