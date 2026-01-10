package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.model.Penalty;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.port.EmailNotificationPort;
import gr.hua.dit.StudyRooms.core.port.HolidayPort;
import gr.hua.dit.StudyRooms.core.repository.BookingRepository;
import gr.hua.dit.StudyRooms.core.repository.PenaltyRepository;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.repository.RoomRepository;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.model.BookingRequest;
import gr.hua.dit.StudyRooms.core.service.model.BookingResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final int MAX_ACTIVE_BOOKINGS_PER_DAY = 2;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PersonRepository personRepository;

    private final PenaltyRepository penaltyRepository;
    private final PenaltyServiceImpl penaltyServiceImpl;

    private final HolidayPort holidayPort;
    private final EmailNotificationPort emailNotificationPort;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            PersonRepository personRepository,
            PenaltyRepository penaltyRepository,
            PenaltyServiceImpl penaltyServiceImpl,
            HolidayPort holidayPort,
            EmailNotificationPort emailNotificationPort
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.personRepository = personRepository;
        this.penaltyRepository = penaltyRepository;
        this.penaltyServiceImpl = penaltyServiceImpl;
        this.holidayPort = holidayPort;
        this.emailNotificationPort = emailNotificationPort;
    }

    @Override
    public BookingResult bookRoom(BookingRequest request) {

        if (request == null) return BookingResult.failed("Request is null");

        Room room = roomRepository.findById(request.roomId()).orElseThrow();
        Person student = personRepository.findById(request.studentId()).orElseThrow();

        LocalDate date = request.date();
        LocalTime start = request.startTime();
        LocalTime end = request.endTime();

        if (date == null || start == null || end == null) {
            return BookingResult.failed("Missing date/time");
        }

        if (!end.isAfter(start)) {
            return BookingResult.failed("End time must be after start time");
        }

        // 1) Holiday check (GET external service)
        if (holidayPort.isHoliday(date)) {
            return BookingResult.failed("Cannot book on holidays.");
        }

        // 2) Penalty check
        if (penaltyServiceImpl.hasActivePenalty(student)) {
            return BookingResult.failed("Δεν μπορείτε να κάνετε κράτηση επειδή έχετε ενεργό penalty.");
        }

        // 3) Room hours check
        LocalTime open = room.getOpenTime();
        LocalTime close = room.getCloseTime();

        if (open != null && start.isBefore(open)) {
            return BookingResult.failed("Cannot book before room opens.");
        }
        if (close != null && end.isAfter(close)) {
            return BookingResult.failed("Cannot book after room closes.");
        }

        // 4) Max active bookings per day (student)
        long activeBookingsToday = bookingRepository.findByStudent(student).stream()
                .filter(b -> !b.isCanceled())
                .filter(b -> date.equals(b.getDate()))
                .count();

        if (activeBookingsToday >= MAX_ACTIVE_BOOKINGS_PER_DAY) {
            return BookingResult.failed("Max active bookings per day reached.");
        }

        // 5) Conflict check (same room/date, overlapping)
        boolean conflict = bookingRepository.findByRoomAndDate(room, date).stream()
                .anyMatch(b -> !b.isCanceled()
                        && start.isBefore(b.getEndTime())
                        && end.isAfter(b.getStartTime()));

        if (conflict) {
            return BookingResult.failed("Time slot already booked.");
        }

        // 6) Save booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStudent(student);
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setCanceled(false);
        booking.setCheckedin(false); // σημαντικό για check-in

        bookingRepository.save(booking);

        // 7) Notification (POST external service)
        try {
            emailNotificationPort.sendSms(
                    student.getPhone(),
                    "Booking confirmed for room " + room.getName()
                            + " on " + date + " (" + start + "-" + end + ")"
            );
        } catch (Exception ignored) {
        }

        return BookingResult.success(booking);
    }

    @Override
    public BookingResult cancelBooking(Long bookingId) {

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return BookingResult.failed("Booking not found");
        }

        Booking booking = optionalBooking.get();
        if (booking.isCanceled()) {
            return BookingResult.failed("Booking is already canceled");
        }

        booking.setCanceled(true);
        bookingRepository.save(booking);

        // Optional: send cancel sms
        try {
            emailNotificationPort.sendSms(
                    booking.getStudent().getPhone(),
                    "Booking canceled for room " + booking.getRoom().getName()
                            + " on " + booking.getDate()
                            + " (" + booking.getStartTime() + "-" + booking.getEndTime() + ")"
            );
        } catch (Exception ignored) {
        }

        return BookingResult.success(booking);
    }

    @Override
    public List<Booking> getBookingsForStudent(Long studentId) {
        Person student = personRepository.findById(studentId).orElseThrow();
        return bookingRepository.findByStudent(student);
    }

    // ✅ το abstract method που σου έβγαζε error
    @Override
    public List<Booking> getBookingsByStudent(Person student) {
        return bookingRepository.findByStudent(student);
    }

    @Override
    public List<Booking> getBookingsForRoom(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        return bookingRepository.findByRoomAndDate(room, date);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Set<Long> getAvailableRoomIds(LocalDate date, LocalTime startTime, LocalTime endTime) {

        List<Booking> bookings =
                bookingRepository.findActiveBookingsForTimeSlot(date, startTime, endTime);

        Map<Long, Long> bookingsPerRoom = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getRoom().getId(), Collectors.counting()));

        return roomRepository.findAll().stream()
                .filter(room -> bookingsPerRoom.getOrDefault(room.getId(), 0L) < room.getCapacity())
                .map(Room::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public BookingResult checkIn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isCanceled()) {
            return BookingResult.failed("Η κράτηση έχει ακυρωθεί");
        }

        if (booking.isCheckedin()) {
            return BookingResult.failed("Έχει ήδη γίνει check-in");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Αν έχει περάσει το booking -> penalty
        if (today.isEqual(booking.getDate()) && now.isAfter(booking.getEndTime())) {
            createPenalty(booking.getStudent());
            return BookingResult.failed("Η κράτηση χάθηκε συνεπώς δημιουργήθηκε ποινή 1 εβδομάδας");
        }

        booking.setCheckedin(true);
        bookingRepository.save(booking);

        return BookingResult.success(booking);
    }

    private void createPenalty(Person student) {

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1);

        Penalty penalty = new Penalty();
        penalty.setStudent(student);
        penalty.setWeeks(1);
        penalty.setStartDate(start);
        penalty.setEndDate(end);
        penalty.setCanceled(false);

        penaltyRepository.save(penalty);
    }
}