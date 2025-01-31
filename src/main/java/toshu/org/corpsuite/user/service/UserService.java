package toshu.org.corpsuite.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.web.dto.Login;
import toshu.org.corpsuite.web.dto.UserAdd;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(UserAdd userAdd) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(userAdd.getCorporateEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("This email exists already!");
        }

        return userRepository.save(initializeUser(userAdd));
    }

    private User initializeUser(UserAdd userAdd) {
        return User.builder()
                .firstName(userAdd.getFirstName())
                .lastName(userAdd.getLastName())
                .corporateEmail(userAdd.getCorporateEmail())
                .position(userAdd.getPosition())
                .country(userAdd.getCountry())
                .department(userAdd.getDepartment())
                .password(passwordEncoder.encode(userAdd.getPassword()))
                .isActive(userAdd.isActive())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .paidLeaveCount(0)
                .subordinates(new ArrayList<>())
                .computers(new ArrayList<>())
                .openedTickets(new ArrayList<>())
                .assignedTickets(new ArrayList<>())
                .openedRequests(new ArrayList<>())
                .assignedRequests(new ArrayList<>())
                .build();
    }

    public User login(Login login) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(login.getEmail());

        if (optionalUser.isEmpty()) {
            throw new DomainException("Username or password are incorrect!");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new DomainException("Username or password are incorrect!");
        }

        return user;
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}
