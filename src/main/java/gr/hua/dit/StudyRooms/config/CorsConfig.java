package gr.hua.dit.StudyRooms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration ΓΙΑ ΤΟ StudyRooms REST API.
 * - CORS αφορά κυρίως SPA / external clients σε browser.
 * - Για το UI εδώ (Τhymeleaf) δεν χρειάζεται.
 */
@Configuration
public class CorsConfig{

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @SuppressWarnings("NullableProblems")
            @Override
            public void addCorsMappings(final CorsRegistry registry) {

                registry.addMapping("/api/v1/**")
                        /// Ορίζει σε ποια endpoints εφαρμόζεται το CORS
                        // Εδώ: όλα τα endpoints κάτω από /api/v1/
                        .allowedOrigins(
                                "http://localhost",
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:5173"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        //JWT= Authorization header, άρα δεν χρειάζεσαι cookies στο API
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}