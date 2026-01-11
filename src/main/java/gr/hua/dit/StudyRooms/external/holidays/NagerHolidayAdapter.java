package gr.hua.dit.StudyRooms.external.holidays;

import gr.hua.dit.StudyRooms.core.port.HolidayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class NagerHolidayAdapter implements HolidayPort {

    private final RestTemplate restTemplate;

    @Value("${external.holidays.country:GR}")
    private String countryCode;

    public NagerHolidayAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        int year = date.getYear();
        String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/" + countryCode;

        NagerHolidayDto[] holidays = restTemplate.getForObject(url, NagerHolidayDto[].class);
        if (holidays == null) return false;

        Set<LocalDate> holidayDates = new HashSet<>();
        Arrays.stream(holidays)
                .map(h -> LocalDate.parse(h.date()))
                .forEach(holidayDates::add);

        return holidayDates.contains(date);
    }
}