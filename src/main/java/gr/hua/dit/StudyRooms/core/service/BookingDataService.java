package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.service.model.BookingView;

import java.util.List;

/**
 * Service for managing {@code Booking} for data analytics purposes.
 */
public interface BookingDataService {

    List<BookingView> getAllBookings();
}