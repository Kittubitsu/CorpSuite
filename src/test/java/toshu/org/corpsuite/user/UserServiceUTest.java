package toshu.org.corpsuite.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.model.UserPosition;
import toshu.org.corpsuite.user.repository.UserRepository;
import toshu.org.corpsuite.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        assertThrows(DomainException.class, () -> userService.getByEmail(email));
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

        assertInstanceOf(AuthenticationMetadata.class, userService.loadUserByUsername(email));
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

}
