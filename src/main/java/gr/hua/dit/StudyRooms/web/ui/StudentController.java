package gr.hua.dit.StudyRooms.web.ui;

import gr.hua.dit.StudyRooms.core.model.*;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import gr.hua.dit.StudyRooms.core.service.PersonService;
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
    private final PenaltyServiceImpl penaltyServiceImpl;
    private final PersonService personService;

    //Constructor
    public StudentController(RoomService roomService,
                             FavoriteService favoriteService,
                             BookingService bookingService, PenaltyServiceImpl penaltyServiceImpl,PersonService personService) {
        this.roomService = roomService;
        this.favoriteService = favoriteService;
        this.bookingService = bookingService;
        this.penaltyServiceImpl = penaltyServiceImpl;
        this.personService = personService;
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
    ){
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        // if (date == null) date = LocalDate.now();
        //if (startTime == null) startTime = LocalTime.of(10, 0);
        //if (endTime == null) endTime = LocalTime.of(12, 0);

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
    public String profile(
            @SessionAttribute("loggedInUser") Person user,
            Model model
    ){

        //Security
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        model.addAttribute("student", user);
        model.addAttribute("editMode", false);

        return "student-profile";
    }



    @GetMapping("/profile/edit")
    public String editProfile(
            @SessionAttribute("loggedInUser") Person user,
            Model model
    ){

        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        model.addAttribute("student", user);
        model.addAttribute("editMode", true);

        return "student-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @SessionAttribute("loggedInUser") Person user,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            Model model
    ){

        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }


        personService.updateStudentProfile(
                user,
                firstName,
                lastName,
                email,
                phone
        );

        model.addAttribute("student", user);
        model.addAttribute("editMode", false);
        model.addAttribute("successMessage", "Τα στοιχεία σάς ενημερώθηκαν!!!");

        //return "redirect:/student/profile";
        return "student-profile";
    }



    @GetMapping("/bookings")
    public String studentBookings(
            @SessionAttribute("loggedInUser") Person user,
            Model model
    ){

        //Security
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        //Ελέγχουμε για penalty αυτόματα
        bookingService.checkAndApplyPenalties(user);

        //Όλες οι κρατήσεις για τον φοιτητή
        List<Booking> bookings = bookingService.getBookingsByStudent(user);

        model.addAttribute("bookings", bookings);

        return "student-bookings";
    }

    //CreateRoom
    @GetMapping("/bookings/create/{roomId}")
    public String showCreateBookingForm(
            @PathVariable Long roomId,
            @SessionAttribute("loggedInUser") Person student,
            Model model
    ) {
        if (student.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        Room room = roomService.getRoomById(roomId);

        model.addAttribute("room", room);
        model.addAttribute("today", LocalDate.now());

        return "student-booking-create";
    }


    @PostMapping("/bookings/create/{roomId}")
    public String createBooking(
            @PathVariable Long roomId,
            @SessionAttribute("loggedInUser") Person student,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {
        if (student.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        if (date == null || startTime == null || endTime == null) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("today", LocalDate.now());
            //model.addAttribute("errorMessage", "Συμπλήρωσε ημερομηνία και ώρες.");
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

        //Πέρασμα του κατάλληλου error.
        if (!result.success()) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("errorMessage", result.message());
            model.addAttribute("today", LocalDate.now());
            return "student-booking-create";
        }
        //Επιτυχία Δημιουργείας Κράτησης
        return "redirect:/student/bookings";
    }




    @PostMapping("/bookings/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            @SessionAttribute("loggedInUser") Person user,
            RedirectAttributes redirectAttributes
    ){

        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

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
            @SessionAttribute("loggedInUser") Person user,
            RedirectAttributes redirectAttributes
    ) {

        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

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


    //Προβολή Penalties (O φοιτητης δεν μπορεί να μεταβάλλει τα penalties ή να τα διαγράψει, μόνο η γραμματεία)
    @GetMapping("/penalties")
    public String myPenalties(
            @SessionAttribute("loggedInUser") Person user,
            Model model
    ){


        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        List<Penalty> penalties = penaltyServiceImpl.getPenaltiesForStudent(user);

        model.addAttribute("penalties", penalties);
        model.addAttribute("today", LocalDate.now());

        return "student-penalties";

    }





}
























