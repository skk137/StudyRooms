package gr.hua.dit.StudyRooms.web.rest.model;

import gr.hua.dit.StudyRooms.web.rest.ClientAuthResource;

// DTO που αναπαριστά την απάντηση έκδοσης token για API client
// Επιστρέφεται από το ClientAuthResource μετά από επιτυχή authentication
public record ClientTokenResponse(

        // JWT access token που θα χρησιμοποιείται σε Authorization: Bearer <token>
        String accessToken,

        // Τύπος token
        String tokenType,

        // Χρόνος ζωής του token σε δευτερόλεπτα
        long expiresIn

) {}