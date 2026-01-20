package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.service.BookingDataService;
import gr.hua.dit.StudyRooms.core.service.model.BookingView;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller για διαχείριση και ανάκτηση κρατήσεων (bookings)
// Εκθέτει endpoints κάτω από το /api/v1/booking
@RestController
@RequestMapping(
        value = "/api/v1/booking",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookingResource {

    // Service που περιέχει τη business logic για τα bookings
    private final BookingDataService bookingDataService;

    // Constructor  του BookingDataService
    public BookingResource(final BookingDataService bookingDataService) {
        if (bookingDataService == null) throw new NullPointerException();
        this.bookingDataService = bookingDataService;
    }

    // Endpoint για ανάκτηση όλων των κρατήσεων
    // Προσβάσιμο μόνο από clients με ρόλο INTEGRATION_READ
    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<BookingView> bookings() {

        // Επιστρέφει λίστα κρατήσεων σε μορφή BookingView
        return this.bookingDataService.getAllBookings();
    }
}