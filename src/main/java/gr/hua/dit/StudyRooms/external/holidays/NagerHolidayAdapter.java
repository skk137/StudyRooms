package gr.hua.dit.StudyRooms.external.holidays;

import gr.hua.dit.StudyRooms.core.port.HolidayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Adapter που υλοποιεί το HolidayPort χρησιμοποιώντας εξωτερική υπηρεσία
@Component
public class NagerHolidayAdapter implements HolidayPort {

    // RestTemplate για κλήσεις σε εξωτερικά REST APIs
    private final RestTemplate restTemplate;

    // Κωδικός χώρας για ανάκτηση αργιών GR
    @Value("${external.holidays.country:GR}")
    private String countryCode;

    // Constructor του RestTemplate
    public NagerHolidayAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Ελέγχει αν μια συγκεκριμένη ημερομηνία είναι επίσημη αργία
    @Override
    public boolean isHoliday(LocalDate date) {

        // Ανάκτηση έτους από την ημερομηνία
        int year = date.getYear();

        // Δημιουργία URL για το Nager.Date API
        String url =
                "https://date.nager.at/api/v3/PublicHolidays/"
                        + year + "/" + countryCode;

        // Κλήση του εξωτερικού API και ανάκτηση λίστας αργιών
        NagerHolidayDto[] holidays =
                restTemplate.getForObject(url, NagerHolidayDto[].class);

        // Αν δεν επιστραφούν δεδομένα, θεωρείται ότι δεν υπάρχει αργία
        if (holidays == null) return false;

        // Συλλογή όλων των ημερομηνιών αργιών σε Set για γρήγορο έλεγχο
        Set<LocalDate> holidayDates = new HashSet<>();

        Arrays.stream(holidays)
                .map(h -> LocalDate.parse(h.date()))
                .forEach(holidayDates::add);

        // Έλεγχος αν η δοσμένη ημερομηνία ανήκει στις αργίες
        return holidayDates.contains(date);
    }
}