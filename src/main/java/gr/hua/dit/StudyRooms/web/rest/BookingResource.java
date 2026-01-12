package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.service.BookingDataService;
import gr.hua.dit.StudyRooms.core.service.model.BookingView;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/booking", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingResource {

    private final BookingDataService bookingDataService;

    public BookingResource(final BookingDataService bookingDataService) {
        if (bookingDataService == null) throw new NullPointerException();
        this.bookingDataService = bookingDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<BookingView> bookings() {
        return this.bookingDataService.getAllBookings();
    }
}