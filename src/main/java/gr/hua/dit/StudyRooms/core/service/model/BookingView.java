package gr.hua.dit.StudyRooms.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * BookingView DTO for information that will be exposed.
 */
public record BookingView(
        Long id,
        Long roomId,
        String roomName,
        Long studentId,
        String studentHuaId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        boolean canceled,
        boolean checkedin
) {}