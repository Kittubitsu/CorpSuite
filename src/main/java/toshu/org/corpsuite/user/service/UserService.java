package toshu.org.corpsuite.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.exception.SamePasswordException;
import toshu.org.corpsuite.exception.UserAlreadyExistsException;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.web.dto.AddUserRequest;
import toshu.org.corpsuite.web.dto.EditUserRequest;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void addUser(AddUserRequest addUserRequest, User user) {

        Optional<User> optionalUser = userRepository.findUserByCorporateEmail(addUserRequest.getCorporateEmail());

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("This email exists already!");
        }

        User saved = initializeUser(addUserRequest);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("CREATE")
                .module("User")
                .comment("User created with id [%s]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }

    public User initializeUser(AddUserRequest userAdd) {

        if (userAdd.getPosition().equals(UserPosition.MANAGER)) {
            User newManager = User.builder()
                    .firstName(userAdd.getFirstName())
                    .lastName(userAdd.getLastName())
                    .corporateEmail(userAdd.getCorporateEmail())
                    .position(userAdd.getPosition())
                    .country(userAdd.getCountry())
                    .department(userAdd.getDepartment())
                    .password(passwordEncoder.encode(userAdd.getPassword()))
                    .active(userAdd.getActive())
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


            return userRepository.save(newManager);
        }

        Optional<User> departmentManagerOptional = getDepartmentManager(userAdd.getDepartment());

        User user = new User();

        if (departmentManagerOptional.isPresent()) {
            User departmentManager = departmentManagerOptional.get();

            user.setManager(departmentManager);

            departmentManager.getSubordinates().add(user);

            userRepository.save(departmentManager);
        }

        user.setFirstName(userAdd.getFirstName());
        user.setLastName(userAdd.getLastName());
        user.setCorporateEmail(userAdd.getCorporateEmail());
        user.setPosition(userAdd.getPosition());
        user.setCountry(userAdd.getCountry());
        user.setDepartment(userAdd.getDepartment());
        user.setPassword(passwordEncoder.encode(userAdd.getPassword()));
        user.setActive(userAdd.getActive());
        user.setCreatedOn(LocalDateTime.now());
        user.setUpdatedOn(LocalDateTime.now());
        user.setPaidLeaveCount(20);
        user.setOpenedTickets(new ArrayList<>());
        user.setAssignedTickets(new ArrayList<>());
        user.setOpenedRequests(new ArrayList<>());
        user.setAssignedRequests(new ArrayList<>());
        user.setCard(userAdd.getCard());
        user.setProfilePicture(userAdd.getProfilePicture());

        return userRepository.save(user);
    }

    public List<User> getAllUsers(Boolean show) {
        List<User> users = userRepository.findAll().stream().filter(user -> !user.getPosition().equals(UserPosition.ADMIN)).toList();

        if (!show) {
            return users.stream().filter(User::isActive).toList();
        }

        return users;
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User could not be found!"));
    }

    public User getByEmail(String email) {
        return userRepository.findUserByCorporateEmail(email).orElseThrow(() -> new UsernameNotFoundException("User could not be found!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //USERNAME IS EMAIL IN MY CASE

        User user = getByEmail(username);

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

    public void editUser(UUID id, EditUserRequest userRequest, User mainUser) {
        User userToEdit = getById(id);

        if (!userRequest.getPassword().isEmpty()) {
            if (passwordEncoder.matches(userRequest.getPassword(), userToEdit.getPassword())) {
                throw new SamePasswordException("Cannot use the same password!");
            }

            userToEdit.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        }


        //When not an admin/hr role, these return as null from the form, so they need to be reassigned.
        if (userRequest.getDepartment() == null && userRequest.getActive() == null && userRequest.getCorporateEmail() == null && userRequest.getPosition() == null && userRequest.getCard() == null) {
            userRequest.setDepartment(userToEdit.getDepartment());
            userRequest.setActive(userToEdit.isActive());
            userRequest.setCorporateEmail(userToEdit.getCorporateEmail());
            userRequest.setPosition(userToEdit.getPosition());
            userRequest.setCard(userToEdit.getCard());
        }

        if (!userRequest.getActive()) {
            userRequest.setCard(null);
            userToEdit.setLeftOn(LocalDateTime.now());
        }

        if (!userToEdit.isActive() && userRequest.getActive()) {
            userToEdit.setLeftOn(null);
        }

        if (userToEdit.getPosition().equals(UserPosition.MANAGER) && !userRequest.getActive()) {
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
        userToEdit.setActive(userRequest.getActive());
        userToEdit.setCountry(userRequest.getCountry());
        userToEdit.setCard(userRequest.getCard());
        userToEdit.setCorporateEmail(userRequest.getCorporateEmail());
        userToEdit.setProfilePicture(userRequest.getProfilePicture());
        userToEdit.setPosition(userRequest.getPosition());
        userToEdit.setUpdatedOn(LocalDateTime.now());

        userRepository.save(userToEdit);

        LogRequest logRequest = LogRequest.builder()
                .email(mainUser.getCorporateEmail())
                .action("EDIT")
                .module("User")
                .comment("User edited with id [%s]".formatted(userToEdit.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
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

        User user = getById(id);
        user.setPaidLeaveCount(user.getPaidLeaveCount() - paidLeaveCount);
        userRepository.save(user);
    }

    public void addUserPaidLeave(UUID id, int paidLeaveCount) {

        User user = getById(id);
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
