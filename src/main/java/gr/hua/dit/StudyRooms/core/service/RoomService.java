package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.service.model.RoomRequest;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();
    Room createRoom(RoomRequest request);
    Room getRoomById(Long id);
    void updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);
}
