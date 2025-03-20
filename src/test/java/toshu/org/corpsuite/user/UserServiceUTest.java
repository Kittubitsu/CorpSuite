package toshu.org.corpsuite.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.exception.SamePasswordException;
import toshu.org.corpsuite.exception.UserAlreadyExistsException;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddUserRequest;
import toshu.org.corpsuite.web.dto.EditUserRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserService userService;


    @Test
    void givenBoolTrue_whenGettingAllUsers_thenGetUnfilteredUsers() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build()
        ));

        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.getAllUsers(true);

        assertEquals(users.size(), actualUsers.size());
        verify(userRepository, times(1)).findAll();

    }

    @Test
    void givenBoolFalse_whenGettingAllUsers_thenGetFilteredOnlyActiveUsers() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build()
        ));

        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.getAllUsers(false);

        assertEquals(users.size() - 2, actualUsers.size());
        verify(userRepository, times(1)).findAll();

    }

    @Test
    void givenMissingUser_whenGettingById_thenThrows() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.getById(id));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void givenExistingUser_whenGettingById_thenReturnsUser() {
        UUID id = UUID.randomUUID();

        User user = User.builder().id(id).build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User actualUser = userService.getById(id);

        assertEquals(user.getId(), actualUser.getId());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void givenMissingUser_whenGettingByEmail_thenThrows() {
        String email = "toshu@abv.bg";

        when(userRepository.findUserByCorporateEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getByEmail(email));
        verify(userRepository, times(1)).findUserByCorporateEmail(any());
    }

    @Test
    void givenExistingUser_whenGettingByEmail_thenReturnsUser() {
        String email = "toshu@abv.bg";

        User user = User.builder().id(UUID.randomUUID()).corporateEmail(email).build();

        when(userRepository.findUserByCorporateEmail(email)).thenReturn(Optional.of(user));

        User actualUser = userService.getByEmail(email);

        assertEquals(user.getCorporateEmail(), actualUser.getCorporateEmail());
        verify(userRepository, times(1)).findUserByCorporateEmail(any());
    }

    @Test
    void givenExistingUser_whenLoadingUserByUsername_thenReturnSuccessfully() {
        String email = "toshu@abv.bg";

        User user = User.builder()
                .id(UUID.randomUUID())
                .corporateEmail("toshu@abv.bg")
                .firstName("Toshu")
                .lastName("Dimov")
                .password("123123")
                .department(UserDepartment.IT)
                .active(true)
                .build();

        when(userRepository.findUserByCorporateEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertInstanceOf(AuthenticationMetadata.class, userDetails);
        verify(userRepository, times(1)).findUserByCorporateEmail(any());

        AuthenticationMetadata authenticationMetadata = (AuthenticationMetadata) userDetails;

        assertEquals(user.getId(), authenticationMetadata.getUserId());
        assertEquals(user.getCorporateEmail(), authenticationMetadata.getEmail());
        assertEquals(user.getFirstName() + '.' + user.getLastName(), authenticationMetadata.getUsername());
        assertEquals(user.getPassword(), authenticationMetadata.getPassword());
        assertEquals(user.getDepartment(), authenticationMetadata.getRole());
        assertEquals(user.isActive(), authenticationMetadata.getIsActive());
        assertThat(authenticationMetadata.getAuthorities()).hasSize(1);
        assertEquals("ROLE_IT", authenticationMetadata.getAuthorities().iterator().next().getAuthority());
        assertEquals(user.isActive(), authenticationMetadata.isAccountNonExpired());
        assertEquals(user.isActive(), authenticationMetadata.isAccountNonLocked());
        assertEquals(user.isActive(), authenticationMetadata.isCredentialsNonExpired());
        assertEquals(user.isActive(), authenticationMetadata.isEnabled());

    }

    @Test
    void givenMissingUser_whenLoadingUserByUsername_thenThrows() {

        when(userRepository.findUserByCorporateEmail(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("toshu@abv.bg"));
        verify(userRepository, times(1)).findUserByCorporateEmail(any());
    }

    @Test
    void givenExistingUsers_whenGettingAllActiveUsers_thenReturnListOfUsers() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).build()
        ));

        when(userRepository.findAllByActive()).thenReturn(users);

        List<User> expectedUsers = userService.getAllActiveUsers();

        assertEquals(users.size(), expectedUsers.size());
        verify(userRepository, times(1)).findAllByActive();
    }

    @Test
    void givenExistingUser_whenGettingDepartmentManager_thenReturnUser() {
        User user = User.builder().corporateEmail("toshu@abv.bg").build();

        when(userRepository.findUserByDepartmentAndPosition_Manager(any())).thenReturn(Optional.of(user));

        User actualUser = userService.getDepartmentManager(UserDepartment.IT).get();

        assertEquals(user.getCorporateEmail(), actualUser.getCorporateEmail());
        verify(userRepository, times(1)).findUserByDepartmentAndPosition_Manager(any());
    }

    @Test
    void givenExistingUsers_whenGettingDepartmentUsers_thenReturnUsers() {

        List<User> users = new ArrayList<>(List.of(
                User.builder().active(true).position(UserPosition.JUNIOR).department(UserDepartment.IT).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).department(UserDepartment.IT).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).department(UserDepartment.IT).build(),
                User.builder().active(false).position(UserPosition.JUNIOR).department(UserDepartment.IT).build(),
                User.builder().active(true).position(UserPosition.JUNIOR).department(UserDepartment.IT).build()
        ));

        when(userRepository.findAllByDepartmentAndPositionNot(any(), any())).thenReturn(users);

        List<User> actualUsers = userService.getDepartmentUsersExclPosition(UserDepartment.IT, UserPosition.ADMIN);

        assertFalse(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAllByDepartmentAndPositionNot(any(), any());
    }

    @Test
    void givenExistingUsers_whenGettingUsersWithoutComputer_thenReturnUsers() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().build(),
                User.builder().build()
        ));

        when(userRepository.findAllByComputersEmpty()).thenReturn(users);

        List<User> actualUsers = userService.getUsersWithoutComputer();

        assertFalse(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAllByComputersEmpty();
    }

    @Test
    void givenExistingUsers_whenGettingRandomUserFromDepartment_thenReturnUser() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().build(),
                User.builder().build(),
                User.builder().build()
        ));

        when(userRepository.findAllByDepartment(any())).thenReturn(users);

        User randomUserFromDepartment = userService.getRandomUserFromDepartment(UserDepartment.IT);

        assertTrue(users.contains(randomUserFromDepartment));
        verify(userRepository, times(1)).findAllByDepartment(any());
    }

    @Test
    void givenExistingUser_whenSubtractPaidLeave_thenUpdateProperty() {
        User user = User.builder().paidLeaveCount(5).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.subtractUserPaidLeave(UUID.randomUUID(), 1);

        assertEquals(4, user.getPaidLeaveCount());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingUser_whenAddPaidLeave_thenUpdateProperty() {
        User user = User.builder().paidLeaveCount(4).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userService.addUserPaidLeave(UUID.randomUUID(), 1);

        assertEquals(5, user.getPaidLeaveCount());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingUsers_whenAddPaidLeave_thenUpdatePropertyForAllUsers() {
        List<User> users = new ArrayList<>(List.of(
                User.builder().paidLeaveCount(5).build(),
                User.builder().paidLeaveCount(5).build(),
                User.builder().paidLeaveCount(5).build()
        ));

        when(userRepository.findAllByActive()).thenReturn(users);

        userService.addPaidLeaveToAllUsers();

        assertEquals(25, users.get(0).getPaidLeaveCount());
        assertEquals(25, users.get(1).getPaidLeaveCount());
        assertEquals(25, users.get(2).getPaidLeaveCount());
        verify(userRepository, times(1)).findAllByActive();
        verify(userRepository, times(3)).save(any());
    }

    @Test
    void givenUserIsManager_whenInitializingUser_thenAddDepartmentUsersToHisSubordinates() {
        AddUserRequest addUserRequest = AddUserRequest.builder()
                .firstName("toshu")
                .lastName("dimov")
                .active(true)
                .department(UserDepartment.IT)
                .position(UserPosition.MANAGER)
                .build();

        User manager = User.builder()
                .firstName("toshu")
                .lastName("dimov")
                .active(true)
                .department(UserDepartment.IT)
                .position(UserPosition.MANAGER)
                .build();

        List<User> users = new ArrayList<>(List.of(
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build()
        ));

        when(userRepository.save(any())).thenReturn(manager);
        when(userRepository.findAllByDepartmentAndPositionNot(UserDepartment.IT, UserPosition.MANAGER)).thenReturn(users);

        userService.initializeUser(addUserRequest);

        verify(userRepository, times(5)).save(any());

    }

    @Test
    void givenUserIsNotManager_whenInitializingUser_thenAddDepartmentManagerToHisManagerField() {
        AddUserRequest addUserRequest = AddUserRequest.builder()
                .firstName("toshu")
                .lastName("dimov")
                .active(true)
                .department(UserDepartment.IT)
                .position(UserPosition.SENIOR)
                .build();

        User manager = User.builder()
                .firstName("toshu")
                .lastName("dimov")
                .active(true)
                .department(UserDepartment.IT)
                .position(UserPosition.MANAGER)
                .subordinates(new ArrayList<>())
                .build();

        when(userRepository.save(any())).thenReturn(manager);
        when(userRepository.findUserByDepartmentAndPosition_Manager(any())).thenReturn(Optional.of(manager));

        userService.initializeUser(addUserRequest);

        verify(userRepository, times(2)).save(any());
        assertFalse(manager.getSubordinates().isEmpty());
        assertEquals(UserPosition.SENIOR, manager.getSubordinates().get(0).getPosition());

    }

    @Test
    void givenMissingUser_whenAddingNewUser_thenCompletesSuccessfully() {

        AddUserRequest addUserRequest = AddUserRequest.builder()
                .firstName("toshu")
                .lastName("dimov")
                .active(true)
                .department(UserDepartment.IT)
                .position(UserPosition.SENIOR)
                .build();

        User user = User.builder()
                .corporateEmail("toshu@abv.bg")
                .build();

        when(userRepository.findUserByCorporateEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(User.builder().id(UUID.randomUUID()).build());
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        userService.addUser(addUserRequest, user);

        verify(userRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));


    }

    @Test
    void givenExistingUser_whenAddingDuplicateUser_thenThrows() {

        when(userRepository.findUserByCorporateEmail(any())).thenReturn(Optional.of(new User()));


        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(AddUserRequest.builder().build(), new User()));
    }

    @Test
    void givenExistingUser_whenEditingSamePassword_thenThrows() {
        EditUserRequest userRequest = EditUserRequest.builder().password("123123").build();

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123123").build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThrows(SamePasswordException.class, () -> userService.editUser(UUID.randomUUID(), userRequest, admin));

    }

    @Test
    void givenExistingUser_whenEditingDifferentPassword_thenSetsTheNewPassword() {
        EditUserRequest userRequest = EditUserRequest.builder().password("123123").build();

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123").position(UserPosition.JUNIOR).active(true).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(userRequest.getPassword());

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        assertEquals(userRequest.getPassword(), userToEdit.getPassword());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingUser_whenUserSetToInactive_thenRemoveCardAndSetLeftOnDate() {
        EditUserRequest userRequest = EditUserRequest.builder().password("").active(false).position(UserPosition.JUNIOR).build();

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123").position(UserPosition.JUNIOR).card(new Card()).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        assertNull(userToEdit.getCard());
        assertNotNull(userToEdit.getLeftOn());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingUserThatIsInactive_whenUserSetToActive_thenRemoveLeftOnDate() {
        EditUserRequest userRequest = EditUserRequest.builder().password("").active(true).position(UserPosition.JUNIOR).build();

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123").active(false).leftOn(LocalDateTime.now()).position(UserPosition.JUNIOR).card(new Card()).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        assertNull(userToEdit.getLeftOn());
        assertTrue(userToEdit.isActive());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenExistingManager_whenSetToInactive_thenRemoveSubordinates() {
        EditUserRequest userRequest = EditUserRequest.builder().password("").active(false).build();

        List<User> users = new ArrayList<>(List.of(
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build()
        ));

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123").active(true).position(UserPosition.MANAGER).subordinates(users).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        assertTrue(userToEdit.getSubordinates().isEmpty());
        verify(userRepository, times(4)).save(any());

    }

    @Test
    void givenExistingUser_whenSetToManager_thenSetHisSubordinates() {
        EditUserRequest userRequest = EditUserRequest.builder().password("").position(UserPosition.MANAGER).active(true).build();

        List<User> users = new ArrayList<>(List.of(
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build(),
                User.builder().department(UserDepartment.IT).build()
        ));

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = User.builder().password("123").active(true).position(UserPosition.SENIOR).subordinates(new ArrayList<>()).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));
        when(userRepository.findAllByDepartmentAndPositionNot(any(), any())).thenReturn(users);

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        assertFalse(userToEdit.getSubordinates().isEmpty());
        assertNotNull(users.get(0).getManager());
        assertNotNull(users.get(1).getManager());
        assertNotNull(users.get(2).getManager());

        verify(userRepository, times(5)).save(any());
    }

    @Test
    void givenExistingUserManager_whenSettingProperties_thenReturnCorrectObject() {
        EditUserRequest userRequest = EditUserRequest.builder()
                .firstName("toshu")
                .lastName("dimov")
                .country("BG")
                .password("")
                .position(UserPosition.MANAGER)
                .active(true)
                .department(UserDepartment.IT)
                .corporateEmail("toshu@abv.bg")
                .card(new Card())
                .build();

        User admin = User.builder().corporateEmail("toshu@abv.bg").build();
        User userToEdit = spy(User.builder().password("123").active(true).position(UserPosition.MANAGER).build());

        when(userRepository.findById(any())).thenReturn(Optional.of(userToEdit));

        userService.editUser(UUID.randomUUID(), userRequest, admin);

        verify(userToEdit, atLeastOnce()).setFirstName(userRequest.getFirstName());
        verify(userToEdit, atLeastOnce()).setLastName(userRequest.getLastName());
        verify(userToEdit, atLeastOnce()).setDepartment(userRequest.getDepartment());
        verify(userToEdit, atLeastOnce()).setActive(userRequest.getActive());
        verify(userToEdit, atLeastOnce()).setCountry(userRequest.getCountry());
        verify(userToEdit, atLeastOnce()).setCard(userRequest.getCard());
        verify(userToEdit, atLeastOnce()).setCorporateEmail(userRequest.getCorporateEmail());
        verify(userToEdit, atLeastOnce()).setProfilePicture(userRequest.getProfilePicture());
        verify(userToEdit, atLeastOnce()).setPosition(userRequest.getPosition());
        verify(userToEdit, atLeastOnce()).setUpdatedOn(any());


    }

}
