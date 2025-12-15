package gr.hua.dit.StudyRooms.core.service.model;

import java.time.LocalTime;

public record RoomRequest(
        String name,
        int capacity,
        LocalTime openTime,
        LocalTime closeTime
) {
    public RoomRequest() {
        this("", 0, LocalTime.of(9,0), LocalTime.of(17,0)); // default τιμές
    }
}