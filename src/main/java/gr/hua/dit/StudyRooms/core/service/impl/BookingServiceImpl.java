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

<<<<<<< HEAD
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
=======
        //Έλεγχος για το αν είναι μέσα στα όρια του room το booking.
        if (!isWithinRoomHours(start, end, open, close)) {
            return BookingResult.failed(
                    "Οι ώρες κράτησης πρέπει να είναι μέσα στο εύρος ωρών που είναι ανοιχτό το δωμάτιο (" +
                            open + " - " + close + ")"
            );
        }

        //Να μην μπορεί να κάνει κράτηση για πάνω απο 5 ώρες.
        Duration duration = Duration.between(start, end);
        if (duration.compareTo(Duration.ofHours(5)) > 0) {
            return BookingResult.failed(
                    "Η μέγιστη διάρκεια κράτησης είναι 5 ώρες."
            );
        }


        // Έλεγχος για το εάν υπάρχει ελεύθερος χώρος στο ίδιο διάστημα.
        long overlappingBookings = bookingRepository.findByRoomAndDate(room, date).stream()
                .filter(b -> !b.isCanceled())
                .filter(b ->
                        start.isBefore(b.getEndTime()) &&
                                end.isAfter(b.getStartTime()) &&
                                !end.equals(b.getStartTime()) &&
                                !start.equals(b.getEndTime())
                )
                .count();
        boolean conflict = overlappingBookings >= room.getCapacity();
>>>>>>> origin/main

        if (conflict) {
            return BookingResult.failed("Time slot already booked.");
        }

<<<<<<< HEAD
        // 6) Save booking
=======

        //Εως 3 Κρατήσεις τη μέρα, μπορεί να κάνει ο φοιτητής.
        long dailyBookings = bookingRepository
                .findByStudent(student)
                .stream()
                .filter(b -> !b.isCanceled())
                .filter(b -> b.getDate().equals(date))
                .count();

        if (dailyBookings >= 3) {
            return BookingResult.failed(
                    "Δεν μπορείται να έχετε ενεργές πάνω απο 3 κρατήσεις την ίδια ημέρα."
            );
        }


        //Δημιουργεία Booking
>>>>>>> origin/main
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

    private boolean isWithinRoomHours(
            LocalTime start,
            LocalTime end,
            LocalTime open,
            LocalTime close
    ) {
        //Κανονικό ωράριο
        if (open.isBefore(close)) {
            return !start.isBefore(open) && !end.isAfter(close);
        }

        //Overnight ωράριο (for me)
        return (!start.isBefore(open) || !start.isAfter(close))
                && (!end.isBefore(open) || !end.isAfter(close));
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

<<<<<<< HEAD
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Αν έχει περάσει το booking -> penalty
        if (today.isEqual(booking.getDate()) && now.isAfter(booking.getEndTime())) {
            createPenalty(booking.getStudent());
            return BookingResult.failed("Η κράτηση χάθηκε συνεπώς δημιουργήθηκε ποινή 1 εβδομάδας");
        }

=======
        //επιτυχές check-in
>>>>>>> origin/main
        booking.setCheckedin(true);
        bookingRepository.save(booking);

        return BookingResult.success(booking);
    }

    //Δημιουργία penalty
    @Override
    public void checkAndApplyPenalties(Person student){


        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        //Παίρνουμε όλες τις κρατήσεις του φοιτητή που δεν έχουν ακυρωθεί και δεν έχουν γίνει check-in
        List<Booking> bookingsToCheck = bookingRepository.findByStudent(student).stream()
                .filter(b -> !b.isCanceled())                 //δεν εχει ακυρθωθεί
                .filter(b -> !b.isCheckedin())              // δεν εχει γίνει check-in
                .filter(b -> {
                    // κράτηση που έχει λήξει
                    if (b.getDate().isBefore(today)) return true; // προηγούμενη μέρα
                    if (b.getDate().isEqual(today) && b.getEndTime().isBefore(now)) return true; // σήμερα αλλά πέρασε η ώρα
                    return false;
                })
                .collect(Collectors.toList());

        //Ελέγχουμε αν υπάρχει ήδη ενεργό penalty
        boolean hasActivePenalty = penaltyRepository.findAllByStudent(student).stream()
                .anyMatch(p -> !p.isCanceled() && !p.getEndDate().isBefore(today));

        //  Αν και μονο αν δεν υπάρχει ενεργό penalty, δημιουργούμε νέο
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
<<<<<<< HEAD
}
=======




}








>>>>>>> origin/main
