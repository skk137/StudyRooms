package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.repository.BookingRepository;
import gr.hua.dit.StudyRooms.core.service.BookingDataService;
import gr.hua.dit.StudyRooms.core.service.Mapper.BookingMapper;
import gr.hua.dit.StudyRooms.core.service.model.BookingView;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@Link BookingDataService}.
 */
@Service
public class BookingDataServiceImpl implements BookingDataService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingDataServiceImpl(final BookingRepository bookingRepository,
                                  final BookingMapper bookingMapper) {
        if (bookingRepository == null) throw new NullPointerException();
        if (bookingMapper == null) throw new NullPointerException();
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public List<BookingView> getAllBookings() {
        final List<Booking> bookingList = this.bookingRepository.findAll();
        final List<BookingView> bookingViewList = bookingList
                .stream()
                .map(this.bookingMapper::convertBookingToBookingView)
                .toList();
        return bookingViewList;
    }
}