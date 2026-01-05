package gr.hua.dit.StudyRooms.core.service.impl;

import gr.hua.dit.StudyRooms.core.model.Room;
import gr.hua.dit.StudyRooms.core.repository.RoomRepository;
import gr.hua.dit.StudyRooms.core.service.RoomService;
import gr.hua.dit.StudyRooms.core.service.model.RoomRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room createRoom(RoomRequest request) {
        Room room = new Room();
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setOpenTime(request.openTime());
        room.setCloseTime(request.closeTime());
        return roomRepository.save(room);
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow();
    }

    @Override
    public void updateRoom(Long id, RoomRequest request) {
        Room room = getRoomById(id);
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setOpenTime(request.openTime());
        room.setCloseTime(request.closeTime());
        roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

}


