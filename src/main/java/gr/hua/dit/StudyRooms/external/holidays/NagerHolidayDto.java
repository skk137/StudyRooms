package gr.hua.dit.StudyRooms.external.holidays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO που χρησιμοποιείται για την αποτύπωση της απάντησης
// από το εξωτερικό API Nager.Date
@JsonIgnoreProperties(ignoreUnknown = true)
public record NagerHolidayDto(

        // Ημερομηνία αργίας σε μορφή yyyy-MM-dd
        String date,

        // Τοπική ονομασία της αργίας
        String localName,

        // Διεθνής ονομασία της αργίας
        String name

) {}