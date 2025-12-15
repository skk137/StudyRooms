package gr.hua.dit.StudyRooms.core.service.impl;


import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import gr.hua.dit.StudyRooms.core.repository.PersonRepository;
import gr.hua.dit.StudyRooms.core.service.AuthService;
import gr.hua.dit.StudyRooms.core.service.model.LogInRequest;
import gr.hua.dit.StudyRooms.core.service.model.LoginResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PersonRepository personRepository,
                           PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResult login(LogInRequest request) {

        return personRepository.findByHuaId(request.huaId())
                .map(person -> {
                    if (!passwordEncoder.matches(
                            request.password(),
                            person.getPasswordHash())) {

                        return LoginResult.failed("Wrong password");
                    }
                    return LoginResult.success(person);
                })
                .orElse(LoginResult.failed("User not found"));
    }

    public void createPerson(Person person) {
        //Κωδικοποίηση password πριν αποθήκευση
        person.setPasswordHash(passwordEncoder.encode(person.getPasswordHash()));
        personRepository.save(person);
    }

}
