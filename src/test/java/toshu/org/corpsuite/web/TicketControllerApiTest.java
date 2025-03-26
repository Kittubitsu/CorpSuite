package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
public class TicketControllerApiTest {

    @MockitoBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private History history;

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
                .paidLeaveCount(20)
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
    void getRequestToTicketPage_returnsCorrectModelAndView() throws Exception {

        MockHttpServletRequestBuilder request = get("/tickets")
                .with(user(authenticationMetadata)).flashAttr("user", user)
                .flashAttr("bool", history.isShow());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("ticket"))
                .andExpect(model().attributeExists("tickets"));

        verify(userService, times(1)).getById(any());
        verify(ticketService, times(1)).getAllByUser(any(), anyBoolean());

    }

    @Test
    void getRequestToAddTicketPage_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/tickets/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("ticket-add"))
                .andExpect(model().attributeExists("ticketRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void postValidRequestToAddTicketPage_redirectsToTicketsPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/tickets/add")
                .with(user(authenticationMetadata))
                .formField("department", "IT")
                .formField("comment", "testtesttesttesttest123123123")
                .formField("status", "PENDING")
                .formField("type", "NETWORK")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getByEmail(any())).thenReturn(user);
        when(userService.getRandomUserFromDepartment(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets?show=" + history.isShow()));

        verify(userService, times(1)).getByEmail(any());
        verify(userService, times(1)).getRandomUserFromDepartment(any());
        verify(ticketService, times(1)).addTicket(any(), any(), any());
    }

    @Test
    void postInvalidRequestToAddTicketPage_returnsToAddTicketPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/tickets/add")
                .with(user(authenticationMetadata))
                .formField("department", "IT")
                .formField("comment", "testtesttesttesttest123123123")
                .formField("status", "PENDING")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getByEmail(any())).thenReturn(user);
        when(userService.getRandomUserFromDepartment(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("ticket-add"))
                .andExpect(model().attributeExists("ticketRequest", "endpoint", "method"));

        verify(userService, times(1)).getByEmail(any());
        verify(userService, times(1)).getRandomUserFromDepartment(any());
        verify(ticketService, never()).addTicket(any(), any(), any());

    }

    @Test
    void getRequestToEditTicketPage_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/tickets/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        when(ticketService.getById(any())).thenReturn(Ticket.builder().requester(user).responsible(user).build());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("ticket-add"))
                .andExpect(model().attributeExists("ticketRequest", "endpoint", "method"));

        verify(ticketService, times(1)).getById(any());
    }

    @Test
    void putRequestToEditTicketPage_redirectsToTicketsPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/tickets/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("department", "IT")
                .formField("comment", "testtesttesttesttest123123123")
                .formField("status", "PENDING")
                .formField("type", "NETWORK")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doNothing().when(ticketService).editTicket(any(), any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(ticketService, times(1)).editTicket(any(), any(), any());

    }

    @Test
    void putInvalidRequestToEditTicketPage_returnsToTicketEditPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/tickets/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("department", "IT")
                .formField("comment", "testtesttesttesttest123123123")
                .formField("status", "PENDING")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("ticket-add"))
                .andExpect(model().attributeExists("ticketRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
        verify(ticketService, never()).editTicket(any(), any(), any());

    }
}
