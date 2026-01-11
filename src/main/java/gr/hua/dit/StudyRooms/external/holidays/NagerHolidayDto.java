package gr.hua.dit.StudyRooms.external.holidays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NagerHolidayDto(String date, String localName, String name) {}