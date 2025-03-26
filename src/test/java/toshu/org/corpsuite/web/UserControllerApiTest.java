package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.exception.SamePasswordException;
import toshu.org.corpsuite.exception.UserAlreadyExistsException;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private History history;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private User unauthorizedUser;

    private AuthenticationMetadata authenticationMetadata;

    private AuthenticationMetadata unauthorizedAuthenticationMetadata;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .corporateEmail("test@corpsuite.com")
                .firstName("test")
                .lastName("user")
                .password("test123123")
                .department(UserDepartment.HR)
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

        unauthorizedAuthenticationMetadata = new AuthenticationMetadata(
                unauthorizedUser.getId(),
                unauthorizedUser.getCorporateEmail(),
                unauthorizedUser.getFirstName(),
                unauthorizedUser.getPassword(),
                unauthorizedUser.getDepartment(),
                unauthorizedUser.isActive());
    }

    @Test
    void getAuthenticatedRequestToUsersEndpoint_returnsUsersPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/users")
                .with(user(authenticationMetadata))
                .flashAttr("user", user)
                .flashAttr("bool", history.isShow());

        when(userService.getAllUsers(anyBoolean())).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).getAllUsers(anyBoolean());
    }

    @Test
    void getUnauthorizedRequestToUsersEndpoint_returnsUsersPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/users")
                .with(user(unauthorizedAuthenticationMetadata))
                .flashAttr("user", user)
                .flashAttr("bool", history.isShow());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, never()).getAllUsers(anyBoolean());
    }

    @Test
    void getRequestToUserEditEndpoint_returnsUserEditPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/users/edit/{id}", UUID.randomUUID())
                .with(user(unauthorizedAuthenticationMetadata))
                .flashAttr("user", user);

        when(userService.getById(any())).thenReturn(user);
        when(cardService.getAllFreeCards(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("userRequest", "method", "cards", "endpoint"));

        verify(userService, times(1)).getById(any());
        verify(cardService, times(1)).getAllFreeCards(any());
    }

    @Test
    void putInvalidRequestToUserEditEndpoint_returnsUserEditPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/users/edit/{id}", UUID.randomUUID())
                .with(user(unauthorizedAuthenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        when(cardService.getAllFreeCards(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("userRequest", "method", "cards", "endpoint"));

        verify(userService, times(1)).getById(any());
        verify(cardService, times(1)).getAllFreeCards(any());
    }

    @Test
    void putValidRequestToUserEditEndpoint_redirectsToHome() throws Exception {
        MockHttpServletRequestBuilder request = put("/users/edit/{id}", UUID.randomUUID())
                .with(user(unauthorizedAuthenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("corporateEmail", "test@test.test")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(unauthorizedUser);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, times(1)).getById(any());
        verify(cardService, never()).getAllFreeCards(any());
        verify(userService, times(1)).editUser(any(), any(), any());
    }

    @Test
    void putValidRequestToUserEditEndpointAndUserIsHR_redirectsToUsersPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/users/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("corporateEmail", "test@test.test")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(cardService, never()).getAllFreeCards(any());
        verify(userService, times(1)).editUser(any(), any(), any());
    }

    @Test
    void putValidRequestToUserEditEndpointAndUserIsADMIN_redirectsToUsersPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/users/edit/{id}", UUID.randomUUID())
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("corporateEmail", "test@test.test")
                .flashAttr("user", user)
                .with(csrf());

        user.setDepartment(UserDepartment.ADMIN);

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(cardService, never()).getAllFreeCards(any());
        verify(userService, times(1)).editUser(any(), any(), any());
    }

    @Test
    void putValidRequestToUserEditEndpointWithInvalidPassword_redirectsToUserEditPage() throws Exception {
        UUID id = UUID.randomUUID();

        MockHttpServletRequestBuilder request = put("/users/edit/{id}", id)
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("corporateEmail", "test@test.test")
                .formField("password", "123123123")
                .flashAttr("user", user)
                .with(csrf());

        user.setDepartment(UserDepartment.ADMIN);

        when(userService.getById(any())).thenReturn(user);
        doThrow(SamePasswordException.class).when(userService).editUser(any(), any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/edit/" + id));

        verify(userService, times(1)).getById(any());
        verify(cardService, never()).getAllFreeCards(any());
        verify(userService, times(1)).editUser(any(), any(), any());
    }

    @Test
    void getAuthenticatedRequestToUserAddEndpoint_returnsUserAddPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/users/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user);

        when(userService.getById(any())).thenReturn(user);
        when(cardService.getAllFreeCards(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("userRequest", "cards", "method", "endpoint"));

        verify(userService, times(1)).getById(any());
        verify(cardService, times(1)).getAllFreeCards(any());
    }

    @Test
    void getUnauthorizedRequestToUserAddEndpoint_returnsToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/users/add")
                .with(user(unauthorizedAuthenticationMetadata))
                .flashAttr("user", user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, never()).getById(any());
        verify(cardService, never()).getAllFreeCards(any());
    }

    @Test
    void postValidRequestToUserAddEndpoint_addsUserAndRedirectsToUsersPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/users/add")
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("department", "IT")
                .formField("corporateEmail", "test@test.test")
                .formField("password", "123123123")
                .formField("position", "JUNIOR")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doNothing().when(userService).addUser(any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(userService, times(1)).addUser(any(), any());
        verify(cardService, never()).getAllCards(anyBoolean());
    }

    @Test
    void postInvalidRequestToUserAddEndpoint_returnsToEditUserPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/users/add")
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("department", "IT")
                .formField("corporateEmail", "test@test.test")
                .formField("password", "123123123")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        when(cardService.getAllCards(anyBoolean())).thenReturn(new ArrayList<>());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("userRequest", "cards", "method", "endpoint"));

        verify(userService, times(1)).getById(any());
        verify(userService, never()).addUser(any(), any());
        verify(cardService, times(1)).getAllCards(anyBoolean());
    }

    @Test
    void postValidRequestToUserAddEndpointWithDuplicateUser_redirectBackToUserAddPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/users/add")
                .with(user(authenticationMetadata))
                .formField("firstName", "test")
                .formField("lastName", "test")
                .formField("country", "Bulgaria")
                .formField("department", "IT")
                .formField("corporateEmail", "test@test.test")
                .formField("password", "123123123")
                .formField("position", "JUNIOR")
                .flashAttr("user", user)
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doThrow(UserAlreadyExistsException.class).when(userService).addUser(any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/add"));

        verify(userService, times(1)).getById(any());
        verify(userService, times(1)).addUser(any(), any());
        verify(cardService, never()).getAllCards(anyBoolean());
    }

}
