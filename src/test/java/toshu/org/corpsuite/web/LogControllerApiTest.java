package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import toshu.org.corpsuite.exception.LogServiceConnectionException;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {LogController.class, LogViewController.class})
public class LogControllerApiTest {

    @MockitoBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockitoBean
    private LogService logService;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private User unauthorizedUser;

    private AuthenticationMetadata authenticationMetadata;

    private AuthenticationMetadata unauthenticatedAuthenticationMetadata;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .corporateEmail("test@corpsuite.com")
                .firstName("test")
                .lastName("user")
                .password("test123123")
                .department(UserDepartment.IT)
                .active(true)
                .build();

        unauthorizedUser = User.builder()
                .id(UUID.randomUUID())
                .corporateEmail("test@corpsuite.com")
                .firstName("test")
                .lastName("user")
                .password("test123123")
                .department(UserDepartment.PROGRAMMING)
                .active(true)
                .build();


        authenticationMetadata = new AuthenticationMetadata(
                user.getId(),
                user.getCorporateEmail(),
                user.getFirstName(),
                user.getPassword(),
                user.getDepartment(),
                user.isActive());

        unauthenticatedAuthenticationMetadata = new AuthenticationMetadata(
                unauthorizedUser.getId(),
                unauthorizedUser.getCorporateEmail(),
                unauthorizedUser.getFirstName(),
                unauthorizedUser.getPassword(),
                unauthorizedUser.getDepartment(),
                unauthorizedUser.isActive());
    }

    @Test
    void getAuthenticatedRequestToLogPage_returnsCorrectView() throws Exception {
        MockHttpServletRequestBuilder request = get("/logs").with(user(authenticationMetadata)).flashAttr("user", user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("log"));
    }

    @Test
    void getUnauthenticatedRequestToLogPage_redirectsToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/logs").with(user(unauthenticatedAuthenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void getUnauthenticatedRequestToAllLogs_redirectsToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/v1/logs").with(user(unauthenticatedAuthenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(logService, never()).getAllLogs();

    }

    @Test
    void getAuthenticatedRequestToAllLogs_returnsListOfLogs() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/v1/logs").with(user(authenticationMetadata));

        when(logService.getAllLogs()).thenReturn(new ArrayList<>(List.of(Log.builder().build(), Log.builder().build())));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        verify(logService, times(1)).getAllLogs();

    }

    @Test
    void deleteUnauthenticatedRequestToDeleteAllLogs_returnsToHome() throws Exception {
        MockHttpServletRequestBuilder request = delete("/api/v1/logs").with(user(unauthenticatedAuthenticationMetadata)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(logService, never()).deleteAllLogs();

    }

    @Test
    void deleteAuthenticatedRequestToDeleteAllLogs_returnsToHome() throws Exception {
        MockHttpServletRequestBuilder request = delete("/api/v1/logs").with(user(authenticationMetadata)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        verify(logService, times(1)).deleteAllLogs();

    }

    @Test
    void getAuthenticatedRequestToAllLogsWithNoConnection_returnsRedirectsToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/v1/logs").with(user(authenticationMetadata));

        when(logService.getAllLogs()).thenThrow(LogServiceConnectionException.class);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(logService, times(1)).getAllLogs();

    }
}
