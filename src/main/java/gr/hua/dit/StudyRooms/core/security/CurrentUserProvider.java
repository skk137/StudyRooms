package gr.hua.dit.StudyRooms.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class CurrentUserProvider {

    public Optional<CurrentUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof ApplicationUserDetails userDetails) {
            return Optional.of(new CurrentUser(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getPersonType()
            ));
        }

        return Optional.empty();
    }
}
