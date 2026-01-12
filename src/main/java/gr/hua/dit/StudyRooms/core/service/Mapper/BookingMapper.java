package gr.hua.dit.StudyRooms.core.service.Mapper;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.service.model.BookingView;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingView convertBookingToBookingView(final Booking booking) {
        if (booking == null) return null;

        return new BookingView(
                booking.getId(),
                booking.getRoom() != null ? booking.getRoom().getId() : null,
                booking.getRoom() != null ? booking.getRoom().getName() : null,
                booking.getStudent() != null ? booking.getStudent().getId() : null,
                booking.getStudent() != null ? booking.getStudent().getHuaId() : null,
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.isCanceled(),
                booking.isCheckedin()
        );
    }
}