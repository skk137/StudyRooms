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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final int MAX_ACTIVE_BOOKINGS_PER_DAY = 2; //μέγιστος αριθμός bookings ανα ημέρα.

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PersonRepository personRepository;

    private final PenaltyRepository penaltyRepository;
    private final PenaltyServiceImpl penaltyServiceImpl;

    private final HolidayPort holidayPort;
    private final EmailNotificationPort emailNotificationPort;

    //Constructor.
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
            return BookingResult.failed("Λείπει η ημερομηνία/Ώρα");
        }

        if (!(room.getOpenTime().isAfter(room.getCloseTime()))) { //Ο επόμενος έλεγχος θα εκτελείται μόνο για δωμάτια, τα οποία δεν ειναι με overnight ωράριο.
            if (!end.isAfter(start)) {
                return BookingResult.failed("Η ώρα έναρξης κράτησης, πρέπει να είναι πριν την ώρα ");
            }
        }

        // Έλεγχος μέγιστης διάρκειας κράτησης.
        Duration duration = Duration.between(start, end);
        if (duration.compareTo(Duration.ofHours(5)) > 0){
            return BookingResult.failed("Η μέγιστη διάρκεια κράτησης είναι 5 ώρες.");
        }

        // 1) Έλεγχος ημερομηνίας αργίας (GET external service)
        if (holidayPort.isHoliday(date)) {
            return BookingResult.failed("Δέν μπορείτε να κάνετε κράτηση, σε ημερομηνίες επίσημων Αργιών.");
        }

        // Έλεγχος penalty.
        if (penaltyServiceImpl.hasActivePenalty(student)) {
            return BookingResult.failed("Δεν μπορείτε να κάνετε κράτηση, επειδή έχετε ενεργό penalty.");
        }

        // Έλεχγος ωρών κράτησης σε σχέση με το ωράριο του δωματίου. (Κανονικό και overnight ωράρια.)
        LocalTime open = room.getOpenTime();
        LocalTime close = room.getCloseTime();
        if (open != null && close != null) {
            if (!isWithinRoomHours(start, end, open, close)) {
                return BookingResult.failed(
                        "Οι ώρες κράτησης πρέπει να είναι μέσα στο εύρος ωρών που είναι ανοιχτό το δωμάτιο (" +
                                open + " - " + close + ")"
                );
            }
        }

        //Έλεγχος για max active bookings την μέρα.
        long activeBookingsToday = bookingRepository.findByStudent(student).stream()
                .filter(b -> !b.isCanceled())
                .filter(b -> date.equals(b.getDate()))
                .count();

        if (activeBookingsToday >= MAX_ACTIVE_BOOKINGS_PER_DAY) {
            return BookingResult.failed("Δεν μπορείτε να έχετε πάνω από " + MAX_ACTIVE_BOOKINGS_PER_DAY + " ενεργές κρατήσεις την ίδια ημέρα.");
        }

        //Έλεγχος ύπαρξης ανοιχτού timeslot/χωρητικότητας εκείνη την ώρα.
        long overlappingBookings = bookingRepository.findByRoomAndDate(room, date).stream()
                .filter(b -> !b.isCanceled())
                .filter(b -> start.isBefore(b.getEndTime()) && end.isAfter(b.getStartTime()))
                .count();

        if (overlappingBookings >= room.getCapacity()) {
            return BookingResult.failed("Δεν υπάρχει διαθέσιμος χώρος στο δωμάτιο για αυτή τη χρονική περίοδο.");
        }

        //Save booking.
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStudent(student);
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setCanceled(false);
        booking.setCheckedin(false);

        bookingRepository.save(booking);

        //Notification μέσω του NOC (POST external service)
        try {
            emailNotificationPort.sendSms(
                    student.getPhone(),
                    "Booking confirmed for room " + room.getName()
                            + " on " + date + " (" + start + "-" + end + ")"
            );
        } catch (Exception ignored) {
            //Ακόμα και αν δεν σταλθεί το SMS, ΤΟ ROOM δημιουργείται.
        }
        return BookingResult.success(booking);
    }

    private boolean isWithinRoomHours(LocalTime start, LocalTime end, LocalTime open, LocalTime close) {
        // Normal hours
        if (open.isBefore(close)) {
            return !start.isBefore(open) && !end.isAfter(close);
        }

        // Overnight hours
        boolean startOk = !start.isBefore(open) || !start.isAfter(close);
        boolean endOk = !end.isBefore(open) || !end.isAfter(close);
        return startOk && endOk;
    }

    @Override
    public BookingResult cancelBooking(Long bookingId) {

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return BookingResult.failed("Η κράτηση δεν βρέθηκε");
        }

        Booking booking = optionalBooking.get();
        if (booking.isCanceled()) {
            return BookingResult.failed("Η κράτηση έχει ήδη ακυρωθεί");
        }

        booking.setCanceled(true);
        bookingRepository.save(booking);

        try {
            emailNotificationPort.sendSms(
                    booking.getStudent().getPhone(),
                    "Η κράτηση ακυρώθηκε για το δωμάτιο " + booking.getRoom().getName()
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
                .orElseThrow(() -> new RuntimeException("Η κράτηση δεν βρέθηκε"));

        if (booking.isCanceled()) {
            return BookingResult.failed("Η κράτηση έχει ακυρωθεί");
        }

        if (booking.isCheckedin()) {
            return BookingResult.failed("Έχει ήδη γίνει check-in");
        }

        //Επιτυχές check-in
        booking.setCheckedin(true);
        bookingRepository.save(booking);

        return BookingResult.success(booking);
    }

    @Override
    public void checkAndApplyPenalties(Person student) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // όταν η κράτηση δεν εχει γίνει cancel, ούτε checked-in, και έχει λήξει.
        List<Booking> bookingsToCheck = bookingRepository.findByStudent(student).stream()
                .filter(b -> !b.isCanceled())
                .filter(b -> !b.isCheckedin())
                .filter(b -> {
                    if (b.getDate().isBefore(today)) return true;
                    return b.getDate().isEqual(today) && b.getEndTime().isBefore(now);
                })
                .collect(Collectors.toList());

        // Αν υπάρχει ήδη, Penalty δεν βάζουμε νέο.
        boolean hasActivePenalty = penaltyRepository.findAllByStudent(student).stream()
                .anyMatch(p -> !p.isCanceled() && !p.getEndDate().isBefore(today));

        if (!hasActivePenalty && !bookingsToCheck.isEmpty()) {
            Penalty penalty = new Penalty();
            penalty.setStudent(student);
            penalty.setWeeks(2);
            penalty.setStartDate(today);
            penalty.setEndDate(today.plusWeeks(1));
            penalty.setCanceled(false);
            penaltyRepository.save(penalty);
        }
    }
}