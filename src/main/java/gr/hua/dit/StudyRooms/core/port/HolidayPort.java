package gr.hua.dit.StudyRooms.core.port;

import java.time.LocalDate;

public interface HolidayPort {
    boolean isHoliday(LocalDate date);
}