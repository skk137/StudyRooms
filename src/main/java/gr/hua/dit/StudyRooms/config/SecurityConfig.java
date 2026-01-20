package gr.hua.dit.StudyRooms.config;

import gr.hua.dit.StudyRooms.core.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Ρύθμιση Spring Security για το REST API της εφαρμογής.
     *
     * Το συγκεκριμένο SecurityFilterChain:
     * - Εφαρμόζεται μόνο στα endpoints /api/v1/**
     * - Χρησιμοποιεί JWT authentication (stateless)
     * - Επιτρέπει public πρόσβαση σε authentication endpoints και Swagger
     * - Απαιτεί authentication για όλα τα υπόλοιπα API requests
     * - Δεν χρησιμοποιεί form login ή redirects (κατάλληλο για REST)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                // Stateless session (χωρίς HTTP session)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Επιτρέπεται το CORS preflight
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints για authentication (login / refresh token)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Public πρόσβαση στο Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Όλα τα υπόλοιπα API endpoints απαιτούν authentication !
                        .anyRequest().authenticated()
                )

                // Φίλτρο JWT πριν το UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Απενεργοποίηση form login & basic auth
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Ρύθμιση του  Spring Security για το UI της εφαρμογής :
     *
     *  Το συγκεκριμένο SecurityFilterChain:
     * - Εφαρμόζεται σε όλα τα UI endpoints
     * - Εξαιρεί πλήρως το API (/api/v1/**)
     * - Ορίζει public σελίδες (home, login, register και static resources)
     * - Εφαρμόζει έλεγχο πρόσβασης βάσει των ρόλων της εφαρμογής (STUDENT, LITERATURE)
     * - Χρησιμοποιεί form-based authentication
     * - Διαχειρίζεται logout και session invalidation
     */
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/**")
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Το uiChain Δεν πρέπει να χειρίζεται το API.
                        .requestMatchers("/api/v1/**").denyAll()

                        // Swagger / API documentation
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Δημόσιες σελίδες και static resources
                        .requestMatchers("/", "/login", "/register",
                                "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Έλεγχος ρόλων
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/literature/**").hasRole("LITERATURE")
                        /// Όλα τα υπόλοιπα request απαιτούν authentication
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        // Σελίδα login
                        .loginPage("/login")
                        // Endpoint που επεξεργάζεται το login
                        .loginProcessingUrl("/login")
                        // Redirect μετά από επιτυχημένο login
                        .defaultSuccessUrl("/profile", true)
                        // Redirect σε αποτυχία login
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        // Endpoint logout
                        .logoutUrl("/logout")
                        // Redirect μετά το logout
                        .logoutSuccessUrl("/login?logout")
                        // Διαγραφή session cookie
                        .deleteCookies("JSESSIONID")
                        // Ακύρωση HTTP session
                        .invalidateHttpSession(true)
                        .permitAll()
                )

                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    //Κρυπτογράφηση password μέσω μηχανισμού Bcrypt.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Ορίζουμε AuthenticationManager της εφαρμογής,
     * το οποίο χρησιμοποιείται από το Spring Security
     * για τη διαδικασία αυθεντικοποίησης των χρηστών.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}