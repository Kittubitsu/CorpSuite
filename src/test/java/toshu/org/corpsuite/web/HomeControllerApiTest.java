package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(GlobalControllerAdvice.class)
public class HomeControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RequestService requestService;

    @MockitoBean
    private TicketService ticketService;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private AuthenticationMetadata authenticationMetadata;

    @BeforeEach
    void setUp() {
        user = User.builder()
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


    }

    @Test
    void getAuthenticatedRequestToHomeEndpoint_shouldReturnHomeView() throws Exception {


        MockHttpServletRequestBuilder request = get("/home").with(user(authenticationMetadata)).flashAttr("user", user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user", "requestList", "ticketList"));

        verify(userService, times(1)).getById(any());
        verify(requestService, times(1)).getFirstThreePendingRequestsByUser(any());
        verify(ticketService, times(1)).getFirstThreePendingTicketsByUser(any());
    }

    @Test
    void getUnauthenticatedRequestToHomeEndpoint_shouldRedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/home");

        mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("http://localhost/login"));
        verify(userService, never()).getById(any());
    }

    @Test
    void getRequestToHomeEndpointWithInvalidUser_shouldReturnErrorPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/home").with(user(authenticationMetadata));

        when(userService.getById(any())).thenThrow(DomainException.class);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(view().name("generic-error"));
    }

    @Test
    void getRequestToHomeEndpoint_returnsCorrectUserToModel() throws Exception {
        when(userService.getById(any())).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/home").with(user(authenticationMetadata));

        mockMvc.perform(request);
    }

}
