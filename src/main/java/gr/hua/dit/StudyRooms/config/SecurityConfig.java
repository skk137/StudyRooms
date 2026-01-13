package gr.hua.dit.StudyRooms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



//debug : προσωρινό SecurityConfig ωστε να μην κλειδώσουν τα endpoints σε αυτο το σταδιο.
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // TODO API Security (stateless - JWT based)
    // TODO UI Security (stateful - cookie based)

    /**
     *UI chain {@code "/**"} (stateful - cookie based).
     */

    @Bean
    @Order(2)
    public SecurityFilterChain uiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register").permitAll()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/literature/**").hasRole("LITERATURE")
                        .requestMatchers("/profile", "/logout").authenticated()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login") // custom login page (see login.html)
                        .loginProcessingUrl("/login") // POST request target (handled by Spring Security)
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // POST request target (handled by Spring Security)
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                // Disable basic security.
                .httpBasic(basic -> {});

                return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
