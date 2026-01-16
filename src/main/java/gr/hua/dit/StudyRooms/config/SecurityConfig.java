package gr.hua.dit.StudyRooms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration.
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize
public class SecurityConfig {

    /**
     * API chain {@code "/api/v1/**"} (stateless, Basic Auth).
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(final HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .httpBasic(basic -> {}); // ✅ Basic Auth for integration user

        return http.build();
    }

    /**
     * UI chain {@code "/**"} (stateful).
     *
     * NOTE: The StudyRooms app currently uses custom session-based login
     * (see loginController + HttpSession), not Spring Security formLogin.
     * So we keep UI permissive to avoid breaking the existing flow.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger public
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // Public UI pages (your controllers manage access with @SessionAttribute checks)
                        .requestMatchers("/", "/login", "/register").permitAll()

                        // Everything else allowed (your controllers already check roles)
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * Integration user (for REST API).
     * Used by @PreAuthorize("hasRole('INTEGRATION_READ')").
     */
    @Bean
    public UserDetailsService userDetailsService(final PasswordEncoder encoder) {
        final UserDetails integrationUser = User.builder()
                .username("integration")
                .password(encoder.encode("integration123"))
                .roles("INTEGRATION_READ") // -> ROLE_INTEGRATION_READ
                .build();

        return new InMemoryUserDetailsManager(integrationUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}