package toshu.org.corpsuite.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.web.dto.AddUser;
import toshu.org.corpsuite.web.dto.EditUser;

import java.time.LocalDateTime;
import java.util.*;

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

    public void addUser(AddUser addUser) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(addUser.getCorporateEmail());

        if (optionalUser.isPresent()) {
            throw new DomainException("This email exists already!");
        }

        initializeUser(addUser);
    }

    public void initializeUser(AddUser userAdd) {

        if (userAdd.getPosition().equals(UserPosition.ADMIN) && userAdd.getDepartment().equals(UserDepartment.ADMIN)) {
            User user = User.builder()
                    .firstName(userAdd.getFirstName())
                    .lastName(userAdd.getLastName())
                    .corporateEmail(userAdd.getCorporateEmail())
                    .position(userAdd.getPosition())
                    .country(userAdd.getCountry())
                    .department(userAdd.getDepartment())
                    .password(passwordEncoder.encode(userAdd.getPassword()))
                    .active(userAdd.getIsActive())
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .paidLeaveCount(0)
                    .computers(new ArrayList<>())
                    .openedTickets(new ArrayList<>())
                    .assignedTickets(new ArrayList<>())
                    .openedRequests(new ArrayList<>())
                    .assignedRequests(new ArrayList<>())
                    .card(userAdd.getCard())
                    .profilePicture(userAdd.getProfilePicture())
                    .build();

            userRepository.save(user);
            return;
        }

        if (userAdd.getPosition().equals(UserPosition.MANAGER)) {
            User newManager = User.builder()
                    .firstName(userAdd.getFirstName())
                    .lastName(userAdd.getLastName())
                    .corporateEmail(userAdd.getCorporateEmail())
                    .position(userAdd.getPosition())
                    .country(userAdd.getCountry())
                    .department(userAdd.getDepartment())
                    .password(passwordEncoder.encode(userAdd.getPassword()))
                    .active(userAdd.getIsActive())
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

            userRepository.save(newManager);

            List<User> departmentUsers = getDepartmentUsersExclPosition(userAdd.getDepartment(), userAdd.getPosition());
            departmentUsers.forEach(user -> {
                newManager.getSubordinates().add(user);
                user.setManager(newManager);
                userRepository.save(user);
            });

            userRepository.save(newManager);

            return;
        }

        Optional<User> departmentManagerOptional = getDepartmentManager(userAdd.getDepartment());

        if (departmentManagerOptional.isPresent()) {
            User departmentManager = departmentManagerOptional.get();

            User user = User.builder()
                    .firstName(userAdd.getFirstName())
                    .lastName(userAdd.getLastName())
                    .corporateEmail(userAdd.getCorporateEmail())
                    .position(userAdd.getPosition())
                    .country(userAdd.getCountry())
                    .department(userAdd.getDepartment())
                    .password(passwordEncoder.encode(userAdd.getPassword()))
                    .active(userAdd.getIsActive())
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .paidLeaveCount(20)
                    .manager(departmentManager)
                    .computers(new ArrayList<>())
                    .openedTickets(new ArrayList<>())
                    .assignedTickets(new ArrayList<>())
                    .openedRequests(new ArrayList<>())
                    .assignedRequests(new ArrayList<>())
                    .card(userAdd.getCard())
                    .profilePicture(userAdd.getProfilePicture())
                    .build();

            departmentManager.getSubordinates().add(user);

            userRepository.save(departmentManager);
            userRepository.save(user);

            return;
        }

        User user = User.builder()
                .firstName(userAdd.getFirstName())
                .lastName(userAdd.getLastName())
                .corporateEmail(userAdd.getCorporateEmail())
                .position(userAdd.getPosition())
                .country(userAdd.getCountry())
                .department(userAdd.getDepartment())
                .password(passwordEncoder.encode(userAdd.getPassword()))
                .active(userAdd.getIsActive())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .paidLeaveCount(20)
                .computers(new ArrayList<>())
                .openedTickets(new ArrayList<>())
                .assignedTickets(new ArrayList<>())
                .openedRequests(new ArrayList<>())
                .assignedRequests(new ArrayList<>())
                .card(userAdd.getCard())
                .profilePicture(userAdd.getProfilePicture())
                .build();

        userRepository.save(user);

    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User could not be found!"));
    }

    public User findByEmail(String email) {
        return userRepository.findUserByCorporateEmail(email).orElseThrow(() -> new DomainException("No such user exists!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByCorporateEmail(username).orElseThrow(() -> new DomainException("User does not exist!"));

        return new AuthenticationMetadata(user.getId(), user.getCorporateEmail(), user.getFirstName() + "." + user.getLastName(), user.getPassword(), user.getDepartment(), user.isActive());
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findAllByActive();
    }

    public Optional<User> getDepartmentManager(UserDepartment department) {
        return userRepository.findUserByDepartmentAndPosition_Manager(department);
    }

    public List<User> getDepartmentUsersExclPosition(UserDepartment department, UserPosition position) {
        return userRepository.findAllByDepartmentAndPositionNot(department, position);
    }

    public void editUser(UUID id, EditUser userRequest) {
        User userToEdit = findById(id);

        if (!userRequest.getPassword().isEmpty()) {
            if (passwordEncoder.matches(userRequest.getPassword(), userToEdit.getPassword())) {
                throw new DomainException("Cannot use the same password, please try again!");
            }

            userToEdit.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        }

        if (userRequest.getDepartment() == null && userRequest.getIsActive() == null && userRequest.getCorporateEmail() == null && userRequest.getPosition() == null && userRequest.getCard() == null) {
            userRequest.setDepartment(userToEdit.getDepartment());
            userRequest.setIsActive(userToEdit.isActive());
            userRequest.setCorporateEmail(userToEdit.getCorporateEmail());
            userRequest.setPosition(userToEdit.getPosition());
            userRequest.setCard(userToEdit.getCard());
        }

        if (!userRequest.getIsActive()) {
            userRequest.setCard(null);
            userToEdit.setLeftOn(LocalDateTime.now());
            userRequest.setPosition(UserPosition.JUNIOR);
        }

        if (userToEdit.getPosition().equals(UserPosition.MANAGER) && !userRequest.getIsActive()) {
            List<User> subordinates = userToEdit.getSubordinates();
            subordinates.forEach(user -> {
                user.setManager(null);
                userRepository.save(user);
            });
            userToEdit.setSubordinates(new ArrayList<>());
        } else if (userRequest.getPosition().equals(UserPosition.MANAGER) && !userToEdit.getPosition().equals(userRequest.getPosition())) {
            userToEdit.setPosition(userRequest.getPosition());
            userRepository.save(userToEdit);
            List<User> departmentUsersExclPosition = getDepartmentUsersExclPosition(userToEdit.getDepartment(), userToEdit.getPosition());
            userToEdit.setSubordinates(departmentUsersExclPosition);
            departmentUsersExclPosition.forEach(user -> {
                user.setManager(userToEdit);
                userRepository.save(user);
            });
        }

        userToEdit.setFirstName(userRequest.getFirstName());
        userToEdit.setLastName(userRequest.getLastName());
        userToEdit.setDepartment(userRequest.getDepartment());
        userToEdit.setActive(userRequest.getIsActive());
        userToEdit.setCountry(userRequest.getCountry());
        userToEdit.setCard(userRequest.getCard());
        userToEdit.setCorporateEmail(userRequest.getCorporateEmail());
        userToEdit.setProfilePicture(userRequest.getProfilePicture());
        userToEdit.setPosition(userRequest.getPosition());
        userToEdit.setUpdatedOn(LocalDateTime.now());

        userRepository.save(userToEdit);
    }

    public List<User> getUsersWithoutComputer() {
        return userRepository.findAllByComputersEmpty();
    }

    public User getRandomUserFromDepartment(UserDepartment department) {

        List<User> users = userRepository.findAllByDepartment(department);
        Random rnd = new Random();
        int i = rnd.nextInt(users.size());

        return users.get(i);
    }

    public void subtractUserPaidLeave(UUID id, int paidLeaveCount) {

        User user = findById(id);
        user.setPaidLeaveCount(user.getPaidLeaveCount() - paidLeaveCount);
        userRepository.save(user);
    }

    public void addUserPaidLeave(UUID id, int paidLeaveCount) {

        User user = findById(id);
        user.setPaidLeaveCount(user.getPaidLeaveCount() + paidLeaveCount);
        userRepository.save(user);
    }

    @Scheduled(cron = "0 0 0 1 1 *")
    public void addPaidLeaveToAllUsers() {
        for (User activeUser : getAllActiveUsers()) {
            activeUser.setPaidLeaveCount(activeUser.getPaidLeaveCount() + 20);
            userRepository.save(activeUser);
        }
        log.info("Paid Leave days added to all users!");
    }
}
