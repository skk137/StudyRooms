package gr.hua.dit.StudyRooms.core.security;

import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring's {@link UserDetailsService} for providing application users.
 *
 * Supports login by huaId OR email.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public ApplicationUserDetailsService(final PersonRepository personRepository){
        if (personRepository == null) throw new NullPointerException();
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) throw new UsernameNotFoundException("Empty username");

        String u = username.strip();
        if (u.isBlank()) throw new UsernameNotFoundException("Empty username");

        // ✅ login with huaId OR email
        var personOpt = u.contains("@")
                ? personRepository.findByEmailIgnoreCase(u)
                : personRepository.findByHuaIdIgnoreCase(u);

        return personOpt
                .map(person -> new ApplicationUserDetails(
                        person.getId(),
                        person.getHuaId(),           // ✅ κρατάμε principal σταθερό = huaId
                        person.getPasswordHash(),
                        person.getPersonType()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User " + u + " not found"));
    }
}