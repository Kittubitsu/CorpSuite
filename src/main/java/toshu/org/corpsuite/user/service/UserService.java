package toshu.org.corpsuite.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.web.dto.UserAddRequest;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User addUser(UserAddRequest userAddRequest) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(userAddRequest.getCorporateEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("This email exists already!");
        }

        return userRepository.save(initializeUser(userAddRequest));;
    }

    private User initializeUser(UserAddRequest userAddRequest){
        return User.builder()
                .firstName(userAddRequest.getFirstName())
                .lastName(userAddRequest.getLastName())
                .corporateEmail(userAddRequest.getCorporateEmail())
                .position(userAddRequest.getPosition())
                .country(userAddRequest.getCountry())
                .department(userAddRequest.getDepartment())
                .password(passwordEncoder.encode(userAddRequest.getPassword()))
                .isActive(userAddRequest.isActive())
                .build();
    }

}
