package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.Room;

import java.util.List;
import java.util.Set;


public interface FavoriteService {

    void toggleFavorite(Person student, Long roomId);

    Set<Long> getFavoriteRoomIds(Person student);

    List<Room> getFavoriteRooms(Person user);
}
