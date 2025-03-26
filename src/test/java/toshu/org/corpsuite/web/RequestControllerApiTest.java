package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.service.RequestService;
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

@WebMvcTest(RequestController.class)
public class RequestControllerApiTest {

    @MockitoBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RequestService requestService;

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
    void getRequestToAbsencePage_returnsCorrectModelAndView() throws Exception {

        MockHttpServletRequestBuilder request = get("/requests")
                .with(user(authenticationMetadata)).flashAttr("user", user)
                .flashAttr("bool", history.isShow());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request"))
                .andExpect(model().attributeExists("requestList"));

        verify(userService, times(1)).getById(any());
        verify(requestService, times(1)).getAllByUser(any(), any());

    }

    @Test
    void getRequestToAddAbsencePageWithUserManagerNull_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/requests/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request-add"))
                .andExpect(model().attributeExists("absenceRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void getRequestToAddAbsencePageWithUserManagerNotNull_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/requests/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        user.setManager(user);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request-add"))
                .andExpect(model().attributeExists("absenceRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void postRequestToAddAbsencePageWithManagerNull_redirectsToAbsencePage() throws Exception {
        MockHttpServletRequestBuilder request = post("/requests/add")
                .with(user(authenticationMetadata))
                .formField("comment", "testtesttesttest123123123123")
                .formField("status", "PENDING")
                .formField("type", "PAID_LEAVE")
                .formField("fromDate", "2024-10-24")
                .formField("toDate", "2024-10-25")
                .formField("totalDays", "1")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(requestService, times(1)).addRequest(any(), any(), any());
    }

    @Test
    void postRequestToAddAbsencePageWithManagerNotNull_redirectsToAbsencePage() throws Exception {
        MockHttpServletRequestBuilder request = post("/requests/add")
                .with(user(authenticationMetadata))
                .formField("comment", "testtesttesttest123123123123")
                .formField("status", "PENDING")
                .formField("type", "PAID_LEAVE")
                .formField("fromDate", "2024-10-24")
                .formField("toDate", "2024-10-25")
                .formField("totalDays", "1")
                .flashAttr("user", user)
                .with(csrf());

        user.setManager(User.builder().build());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(requestService, times(1)).addRequest(any(), any(), any());
    }

    @Test
    void postInvalidRequestToAddAbsencePageWithManagerNotNull_returnsToAddAbsencePage() throws Exception {
        MockHttpServletRequestBuilder request = post("/requests/add")
                .with(user(authenticationMetadata))
                .formField("comment", "testtesttesttest123123123123")
                .formField("status", "PENDING")
                .formField("type", "PAID_LEAVE")
                .formField("fromDate", "2024-10-24")
                .formField("toDate", "2024-10-25")
                .flashAttr("user", user)
                .with(csrf());

        user.setManager(User.builder().build());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request-add"))
                .andExpect(model().attributeExists("absenceRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
        verify(requestService, never()).addRequest(any(), any(), any());
    }

    @Test
    void getRequestToAbsenceEditPage_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/requests/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        when(requestService.getById(any())).thenReturn(Request.builder().requester(user).responsible(user).build());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request-add"))
                .andExpect(model().attributeExists("absenceRequest", "endpoint", "method"));

        verify(requestService, times(1)).getById(any());
    }

    @Test
    void putRequestToEditAbsencePage_redirectsToAbsencePage() throws Exception {
        MockHttpServletRequestBuilder request = put("/requests/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("comment", "testtesttesttest123123123123")
                .formField("status", "PENDING")
                .formField("type", "PAID_LEAVE")
                .formField("fromDate", "2024-10-24")
                .formField("toDate", "2024-10-25")
                .formField("totalDays", "1")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doNothing().when(requestService).editRequest(any(), any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/requests?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(requestService, times(1)).editRequest(any(), any(), any());

    }

    @Test
    void putInvalidRequestToEditAbsencePage_returnsToAbsenceEditPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/requests/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("comment", "testtesttesttest123123123123")
                .formField("status", "PENDING")
                .formField("type", "PAID_LEAVE")
                .formField("fromDate", "2024-10-24")
                .formField("toDate", "2024-10-25")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("request-add"))
                .andExpect(model().attributeExists("absenceRequest", "endpoint", "method"));

        verify(userService, times(1)).getById(any());
        verify(requestService, never()).editRequest(any(), any(), any());

    }
}
