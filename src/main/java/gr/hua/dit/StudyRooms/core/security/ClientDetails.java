package gr.hua.dit.StudyRooms.core.security;

import java.util.Set;

// Record που αναπαριστά τα στοιχεία ενός API client
// Χρησιμοποιείται στο authentication service-to-service
public record ClientDetails(

        // Αναγνωριστικό του client
        String id,

        // Μυστικό κλειδί του client που χρησιμοποιείται για authentication
        String secret,

        // Σύνολο ρόλων που αποδίδονται στον client
        // Οι ρόλοι χρησιμοποιούνται για authorization
        Set<String> roles

) {}