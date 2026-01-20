package gr.hua.dit.StudyRooms.core.service.Mapper;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.service.model.BookingView;
import org.springframework.stereotype.Component;

// Mapper κλάση που μετατρέπει domain entity Booking σε BookingView
// Χρησιμοποιείται για τη μεταφορά δεδομένων προς το API / UI layer
@Component
public class BookingMapper {

    // Μετατρέπει ένα αντικείμενο Booking σε BookingView
    // Αν το Booking είναι null, επιστρέφεται null
    public BookingView convertBookingToBookingView(final Booking booking) {
        if (booking == null) return null;

        // Δημιουργία BookingView με επιλεγμένα πεδία
        // Γίνονται null checks για τα συσχετισμένα entities (Room, Student)
        return new BookingView(
                booking.getId(),

                // Στοιχεία αίθουσας (Room)
                booking.getRoom() != null ? booking.getRoom().getId() : null,
                booking.getRoom() != null ? booking.getRoom().getName() : null,

                // Στοιχεία φοιτητή (Student)
                booking.getStudent() != null ? booking.getStudent().getId() : null,
                booking.getStudent() != null ? booking.getStudent().getHuaId() : null,

                // Χρονικά στοιχεία κράτησης
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),

                // Κατάσταση κράτησης
                booking.isCanceled(),
                booking.isCheckedin()
        );
    }
}