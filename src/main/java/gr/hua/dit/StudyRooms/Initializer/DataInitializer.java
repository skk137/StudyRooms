package gr.hua.dit.StudyRooms.Initializer;


import gr.hua.dit.StudyRooms.core.model.*;
import gr.hua.dit.StudyRooms.core.repository.BookingRepository;
import gr.hua.dit.StudyRooms.core.repository.PenaltyRepository;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PenaltyRepository penaltyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PersonRepository personRepository,
                           RoomRepository roomRepository,
                           BookingRepository bookingRepository,
                           PenaltyRepository penaltyRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.penaltyRepository = penaltyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- Δημιουργία φοιτητών ---
        Person student1 = new Person(null, "1", PersonType.STUDENT, "Γιώργος", "Παπαδάκης", "papadakis1@hua.gr", "6989159046", passwordEncoder.encode("137"), Instant.now());
        Person student2 = new Person(null, "5", PersonType.STUDENT, "Μαρία", "Κωνσταντίνου", "skkbusiness1377@hua.gr", "698915995", passwordEncoder.encode("137"), Instant.now());
        //Δημιουργία Αρχικού Χρήστη Βιβλιοθήκης
        Person literatureAdmin = new Person(null, "2022095", PersonType.LITERATURE, "Admin", "01", "adminStudyRooms@hua.gr", "698915995", passwordEncoder.encode("137"), Instant.now());

        //Αποθήκευση Χρηστών
        personRepository.save(student1);
        personRepository.save(student2);
        personRepository.save(literatureAdmin);

        // --- Δημιουργία δωματίων ---
        Room room1 = new Room("Orofos 1 Room A", 4, LocalTime.of(8, 0), LocalTime.of(20, 0));
        Room room2 = new Room("Orofos 2 Room B", 6, LocalTime.of(9, 0), LocalTime.of(21, 0));
        Room room3 = new Room("Isogio Room", 2, LocalTime.of(20, 30), LocalTime.of(3, 30));

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        // --- Δημιουργία bookings ---
        Booking booking1 = new Booking(null, room1, student1,
                LocalDate.now(), LocalTime.of(10,0), LocalTime.of(12,0), false,false);

        Booking booking2 = new Booking(null, room2, student2,
                LocalDate.now().plusDays(1), LocalTime.of(14,0), LocalTime.of(16,0), false,false);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        // --- Δημιουργία penalties ---
        Penalty penalty1 = new Penalty(null, student1, 2, false,LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 14));

        Penalty penalty2 = new Penalty(null, student2, 1, false,LocalDate.of(2026, 1, 8),
                LocalDate.of(2026, 1, 15));

        penaltyRepository.save(penalty1);
        penaltyRepository.save(penalty2);

        System.out.println("DataInitializer: Tα αρχικά δεδομένα δημιουργήθηκαν!!!! ");

    }
}
