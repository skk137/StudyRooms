package gr.hua.dit.StudyRooms.web.rest;


import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Λίστα κρατήσεων για το Literature Panel
    @GetMapping("/literature/bookings")
    public String getAllBookings(Model model,@SessionAttribute("loggedInUser") Person user){

        //Δεύτερος έλεγχος (1ος στο LogIn Controller) τύπου person ωστε, ακόμα και αν κάποιος προσπαθήσει να έχει Direct URL access, να αποτραπεί.
        if (user.getPersonType() != PersonType.LITERATURE) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "bookings";
    }


    @PostMapping("/literature/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id,@SessionAttribute("loggedInUser") Person user){

        //Δεύτερος έλεγχος (1ος στο LogIn Controller) τύπου person ωστε, ακόμα και αν κάποιος προσπαθήσει να έχει Direct URL access, να αποτραπεί.
        if (user.getPersonType() != PersonType.LITERATURE) {
            return "redirect:/login";
        }

        bookingService.cancelBooking(id); // Αλλάζει μόνο το πεδίο canceled σε true
        return "redirect:/literature/bookings";
    }

}

