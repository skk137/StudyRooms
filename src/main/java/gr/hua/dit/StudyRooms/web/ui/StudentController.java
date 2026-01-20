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

// UI Controller για λειτουργίες φοιτητή
// Χρησιμοποιεί services για business logic και επιστρέφει templates
@Controller
@RequestMapping("/student")
public class StudentController {

    // Services που χρησιμοποιούνται για πρόσβαση σε δεδομένα και επιχειρησιακή λογική
    private final RoomService roomService;
    private final FavoriteService favoriteService;
    private final BookingService bookingService;
    private final PersonService personService;
    private final PenaltyServiceImpl penaltyService;
    private final CurrentUserProvider currentUserProvider;

    // Constructor  των dependencies
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

    // Βοηθητική μέθοδος: εντοπίζει τον τρέχοντα χρήστη από το SecurityContext
    // και επιστρέφει το αντίστοιχο Person από τη βάση
    private Person requireStudent() {
        var me = currentUserProvider.getCurrentUser().orElseThrow();
        return personService.findById(me.id()).orElseThrow();
    }

    // Dashboard φοιτητή εμφανίζει λίστα δωματίων, favorites και διαθεσιμότητες για default χρονικό διάστημα
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Ανάκτηση τρέχοντος φοιτητή
        Person student = requireStudent();

        // Ημερομηνία και ώρες
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.now().withSecond(0).withNano(0);
        LocalTime end = start.plusHours(1);

        // Δεδομένα για εμφάνιση στο UI
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("favoriteRoomIds", favoriteService.getFavoriteRoomIds(student));
        model.addAttribute("availableRoomIds",
                bookingService.getAvailableRoomIds(date, start, end));

        // Προεπιλεγμένες τιμές φίλτρων
        model.addAttribute("date", date);
        model.addAttribute("startTime", start);
        model.addAttribute("endTime", end);

        return "student-dashboard";
    }

    // Προβολή αγαπημένων δωματίων + ένδειξη διαθεσιμότητας με βάση φίλτρα ημερομηνίας/ώρας
    @GetMapping("/favorites")
    public String favorites(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {

        // Ανάκτηση τρέχοντος φοιτητή
        Person student = requireStudent();

        // Φόρτωση αγαπημένων δωματίων και ids
        List<Room> favoriteRooms = favoriteService.getFavoriteRooms(student);
        Set<Long> favoriteRoomIds = favoriteService.getFavoriteRoomIds(student);

        // Υπολογισμός διαθέσιμων δωματίων με βάση τα φίλτρα
        Set<Long> availableRoomIds =
                bookingService.getAvailableRoomIds(date, startTime, endTime);

        // Δεδομένα για εμφάνιση στο UI
        model.addAttribute("favoriteRooms", favoriteRooms);
        model.addAttribute("favoriteRoomIds", favoriteRoomIds);
        model.addAttribute("availableRoomIds", availableRoomIds);

        // Επιστρέφουμε ξανά τα φίλτρα για να παραμένουν συμπληρωμένα στη φόρμα
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "student-favorites";
    }

    // Προβολή προφίλ φοιτητή σε read-only μορφή
    @GetMapping("/profile")
    public String profile(Model model) {

        Person student = requireStudent();

        model.addAttribute("student", student);
        model.addAttribute("editMode", false);

        return "student-profile";
    }

    // Ενεργοποίηση φόρμας επεξεργασίας προφίλ
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {

        Person student = requireStudent();

        model.addAttribute("student", student);
        model.addAttribute("editMode", true);

        return "student-profile";
    }

    // Υποβολή αλλαγών προφίλ - ενημέρωση στοιχείων
    @PostMapping("/profile/edit")
    public String updateProfile(
            @AuthenticationPrincipal ApplicationUserDetails userDetails,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            Model model
    ){

        // Id του συνδεδεμένου χρήστη από το principal
        Long personId = userDetails.getId();

        // Φόρτωση του Person από τη βάση
        Person student = personService.findById(personId)
                .orElseThrow(() -> new IllegalStateException("Logged-in student not found"));

        // Ενημέρωση προφίλ μέσω service
        personService.updateStudentProfile(
                student,
                firstName,
                lastName,
                email,
                phone
        );

        // Επιστροφή στο ίδιο view με μήνυμα επιτυχίας
        model.addAttribute("student", student);
        model.addAttribute("editMode", false);
        model.addAttribute("successMessage", "Τα στοιχεία σάς ενημερώθηκαν!!! ");

        return "student-profile";
    }

    // Προβολή κρατήσεων φοιτητή
    @GetMapping("/bookings")
    public String bookings(Model model) {

        Person student = requireStudent();

        // Έλεγχος και εφαρμογή ποινών αν απαιτείται πριν την προβολή
        bookingService.checkAndApplyPenalties(student);

        // Ανάκτηση όλων των κρατήσεων του φοιτητή
        List<Booking> bookings = bookingService.getBookingsByStudent(student);

        model.addAttribute("bookings", bookings);
        return "student-bookings";
    }

    // Δημιουργία κράτησης για συγκεκριμένο δωμάτιο
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

    // Υποβολή νέας κράτησης για συγκεκριμένο δωμάτιο
    @PostMapping("/bookings/create/{roomId}")
    public String createBooking(
            @PathVariable Long roomId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            Model model
    ) {
        Person student = requireStudent();

        // Έλεγχος ότι συμπληρώθηκαν όλα τα πεδία
        if (date == null || startTime == null || endTime == null) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("today", LocalDate.now());
            return "student-booking-create";
        }

        // Δημιουργία request model και κλήση service για κράτηση
        BookingResult result = bookingService.bookRoom(
                new BookingRequest(roomId, student.getId(), date, startTime, endTime)
        );

        // Σε αποτυχία, επιστρέφεται η φόρμα με μήνυμα σφάλματος
        if (!result.success()) {
            Room room = roomService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("errorMessage", result.message());
            model.addAttribute("today", LocalDate.now());
            return "student-booking-create";
        }

        // Σε επιτυχία, redirect στη λίστα κρατήσεων
        return "redirect:/student/bookings";
    }

    // Ακύρωση κράτησης
    @PostMapping("/bookings/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        BookingResult result = bookingService.cancelBooking(id);

        // Χρήση flash attributes για μηνύματα μετά από redirect
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

    // Check-in σε κράτηση
    @PostMapping("/bookings/checkin/{id}")
    public String checkIn(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        BookingResult result = bookingService.checkIn(id);

        // Χρήση flash attributes για μηνύματα μετά από redirect
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

    // Προβολή ποινών φοιτητή - μόνο ανάγνωση
     // Ο φοιτητής δεν μπορεί να μεταβάλει ποινές, μόνο να τις βλέπει
    @GetMapping("/penalties")
    public String penalties(Model model) {

        Person student = requireStudent();

        // Ανάκτηση ποινών για τον συγκεκριμένο φοιτητή
        List<Penalty> penalties =
                penaltyService.getPenaltiesForStudent(student);

        model.addAttribute("penalties", penalties);
        model.addAttribute("today", LocalDate.now());

        return "student-penalties";
    }
}