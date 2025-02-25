package toshu.org.corpsuite.user.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.web.dto.EditUser;
import toshu.org.corpsuite.web.dto.UserAdd;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(EditUser addUser) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(addUser.getCorporateEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("This email exists already!");
        }

        userRepository.save(initializeUser(addUser));
    }

    private User initializeUser(EditUser userAdd) {
        return User.builder()
                .firstName(userAdd.getFirstName())
                .lastName(userAdd.getLastName())
                .corporateEmail(userAdd.getCorporateEmail())
                .position(userAdd.getPosition())
                .country(userAdd.getCountry())
                .department(userAdd.getDepartment())
                .password(passwordEncoder.encode(userAdd.getPassword()))
                .isActive(userAdd.getIsActive())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .paidLeaveCount(20)
                .subordinates(new ArrayList<>())
                .computers(new ArrayList<>())
                .openedTickets(new ArrayList<>())
                .assignedTickets(new ArrayList<>())
                .openedRequests(new ArrayList<>())
                .assignedRequests(new ArrayList<>())
                .card(userAdd.getCard())
                .profilePicture(userAdd.getProfilePicture())
                .build();
    }


    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User could not be found!"));
    }

    public User findByEmail(String responsible) {
        return userRepository.findUserByCorporateEmail(responsible).orElseThrow(() -> new DomainException("No such user exists!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByCorporateEmail(username).orElseThrow(() -> new DomainException("User does not exist!"));

        return new AuthenticationMetadata(user.getId(), user.getCorporateEmail(), user.getFirstName() + "." + user.getLastName(), user.getPassword(), user.getDepartment(), user.isActive());
    }

    public List<User> getAllActiveUsers() {

        return userRepository.findAllByActive();
    }
}
