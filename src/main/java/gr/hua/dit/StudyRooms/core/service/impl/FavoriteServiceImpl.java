package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Favorite;
import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.repository.FavoriteRepository;
import gr.hua.dit.StudyRooms.core.repository.RoomRepository;
import gr.hua.dit.StudyRooms.core.service.FavoriteService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RoomRepository roomRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               RoomRepository roomRepository) {
        this.favoriteRepository = favoriteRepository;
        this.roomRepository = roomRepository;
    }

    //
    @Override
    public void toggleFavorite(Person student, Long roomId) {

        //Βρίσκουμε το δωμάτιο με βάση το id
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Το δωμάτιο, δέν βρέθηκε"));

        //Ελέγχουμε αν υπάρχει ήδη εγγραφή Favorite
        favoriteRepository.findByStudentAndRoom(student, room)
                .ifPresentOrElse(
                        //Αν υπάρχει τοτε το βγάζουμε απο αγαπημένα.
                        favoriteRepository::delete,
                        //Αν δεν υπάρχει, δημιουργούμε, νεα εγγραφή favorite.
                        () -> favoriteRepository.save(new Favorite(student, room))
                );
    }

    //Βρίσκουμε τα αγαπημένα δωμάτια, του student.
    @Override
    public Set<Long> getFavoriteRoomIds(Person student) {
        return favoriteRepository.findAllByStudent(student)
                .stream()
                .map(f -> f.getRoom().getId())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Room> getFavoriteRooms(Person student) {

        return favoriteRepository.findAllByStudent(student)
                .stream()
                .map(Favorite::getRoom)
                .toList();
    }

}



