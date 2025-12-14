package gr.hua.dit.StudyRooms.core.service.model;

import java.time.LocalDate;
import java.time.LocalTime;



public record BookingRequest(
        Long roomId,
        Long studentId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {


}