package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.service.model.BookingRequest;
import gr.hua.dit.StudyRooms.core.service.model.BookingResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface BookingService {

    BookingResult bookRoom(BookingRequest request);
    BookingResult cancelBooking(Long bookingId);
    List<Booking> getBookingsForStudent(Long studentId);
    List<Booking> getBookingsForRoom(Long roomId, LocalDate date);
    List<Booking> getAllBookings();


    Set<Long> getAvailableRoomIds(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );



}
