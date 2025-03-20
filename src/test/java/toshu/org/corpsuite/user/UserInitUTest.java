package toshu.org.corpsuite.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserInit;
import toshu.org.corpsuite.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInitUTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserInit userInit;


    @Test
    void givenNoUsers_whenRun_thenCreateFirstUsers() throws Exception {

        when(userService.getAllActiveUsers()).thenReturn(Collections.emptyList());

        userInit.run();

        verify(userService, atLeastOnce()).addUser(any(), any());
    }

    @Test
    void givenUsersExists_whenRun_thenDoNothing() throws Exception {

        when(userService.getAllActiveUsers()).thenReturn(List.of(new User()));

        userInit.run();

        verify(userService, never()).addUser(any(), any());
    }
}
