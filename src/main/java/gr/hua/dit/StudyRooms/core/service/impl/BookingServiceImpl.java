package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Booking;
import gr.hua.dit.StudyRooms.core.model.Penalty;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.repository.BookingRepository;
import gr.hua.dit.StudyRooms.core.repository.PenaltyRepository;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.repository.RoomRepository;
import gr.hua.dit.StudyRooms.core.service.BookingService;
import gr.hua.dit.StudyRooms.core.service.model.BookingRequest;
import gr.hua.dit.StudyRooms.core.service.model.BookingResult;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PersonRepository personRepository;
    private final PenaltyRepository penaltyRepository;
    private final PenaltyServiceImpl penaltyServiceImpl;
    //private final HolidayService holidayService;
    //private final EmailService emailService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            PersonRepository personRepository,
            //, HolidayService holidayService,//EmailService emailService
            PenaltyRepository penaltyRepository, PenaltyServiceImpl penaltyServiceImpl) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.personRepository = personRepository;
        //this.holidayService = holidayService;
        //this.emailService = emailService;
        this.penaltyRepository = penaltyRepository;
        this.penaltyServiceImpl = penaltyServiceImpl;
    }

    @Override
    public BookingResult bookRoom(BookingRequest request) {

        //to do
        //if (holidayService.isHoliday(date)) {
        //    return BookingResult.failed("Cannot book on holidays.");
        //}

        Room room = roomRepository.findById(request.roomId()).orElseThrow();
        Person student = personRepository.findById(request.studentId()).orElseThrow();
        LocalDate date = request.date();
        LocalTime start = request.startTime();
        LocalTime end = request.endTime();

        LocalTime open = room.getOpenTime();
        LocalTime close = room.getCloseTime();

        //Εαν ο φοιτητης έχει κάποιο ενεργό penalty, δεν μπορει να κάνει κράτηση.
        if (penaltyServiceImpl.hasActivePenalty(student)) {
            return BookingResult.failed(
                    "Δεν μπορείτε να κάνετε κράτηση επειδή έχετε ενεργό penalty."
            );
        }
        //Εαν βάλει ωρα τέλους κράτησης πριν την ώρα ένραξης--> σφάλμα.
        if (!start.isBefore(end)) {
            return BookingResult.failed("Η ώρα έναρξης, πρέπει να είναι πριν το τέλος της κράτησης.");
        }

        //Έλεγχος για το αν ειναι μεσα στα ορια του room το booking
        if (start.isBefore(open) || end.isAfter(close)) {
            return BookingResult.failed(
                    "Οι ώρες κράτησης πρέπει να είναι μέσα στο εύρος ωρών που είναι ανοιχτό το δωμάτιο (" +
                            open + " - " + close + ")"
            );
        }

        // Έλεγχος για το εάν υπάρχει ήδη κράτηση στο ίδιο διάστημα
        boolean conflict = bookingRepository.findByRoomAndDate(room, date).stream()
                .anyMatch(b -> !b.isCanceled() &&
                        start.isBefore(b.getEndTime()) &&
                        end.isAfter(b.getStartTime()));

        if (conflict) {
            return BookingResult.failed("Δεν υπάρχει διαθέσιμος χώρος στο δωμάτιο για αυτή τη χρονική περίοδο.");
        }


        //Δημιουργεία Booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStudent(student);
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setCanceled(false);

        bookingRepository.save(booking);


        // αποστολή email to-do
        //emailService.sendBookingConfirmation(student.getEmail(), student.getFirstName(), booking);

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
        return BookingResult.success(booking);
    }


    @Override
    public List<Booking> getBookingsForStudent(Long studentId) {
        Person student = personRepository.findById(studentId).orElseThrow();
        return bookingRepository.findByStudent(student);
    }

    @Override
    public List<Booking> getBookingsForRoom(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        return bookingRepository.findByRoomAndDate(room, date);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll(); //Επιστρέφει όλες τις κρατήσεις
    }


    //debug
    @Override
    public Set<Long> getAvailableRoomIds(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {

        List<Room> allRooms = roomRepository.findAll();

        // Όλες οι ενεργές κρατήσεις που επικαλύπτουν το slot
        List<Booking> bookings =
                bookingRepository.findActiveBookingsForTimeSlot(
                        date, startTime, endTime
                );

        // Map: roomId -> πόσες κρατήσεις έχει
        Map<Long, Long> bookingsPerRoom =
                bookings.stream()
                        .collect(Collectors.groupingBy(
                                b -> b.getRoom().getId(),
                                Collectors.counting()
                        ));

        // Room διαθέσιμο αν κρατήσεις < capacity
        return allRooms.stream()
                .filter(room -> {
                    long currentBookings =
                            bookingsPerRoom.getOrDefault(room.getId(), 0L);
                    return currentBookings < room.getCapacity();
                })
                .map(Room::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Booking> getBookingsByStudent(Person student) {
        return bookingRepository.findByStudent(student);
    }



    @Override
    public BookingResult checkIn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ακυρωμένη
        if (booking.isCanceled()) {
            return BookingResult.failed("Η κράτηση έχει ακυρωθεί");
        }

        // Ηδη checked in
        if (booking.isCheckedin()) {
            return BookingResult.failed("Έχει ήδη γίνει check-in");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Πέρασε το time window άρα δημιουργεία PENALTY
        if (today.isEqual(booking.getDate())
                && now.isAfter(booking.getEndTime())) {

            createPenalty(booking.getStudent());

            return BookingResult.failed("Η κράτηση χάθηκε συνεπώς δημιουργήθηκε ποινή 1 εβδομάδας");
        }

        //επιτυχές check-in
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








