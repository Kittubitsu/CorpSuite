package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.service.ComputerService;
import toshu.org.corpsuite.exception.ComputerAlreadyExistsException;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComputerController.class)
public class ComputerControllerApiTest {

    @MockitoBean
    private GlobalControllerAdvice globalControllerAdvice;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ComputerService computerService;

    @MockitoBean
    private History history;

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
    void getAuthenticatedRequestToComputersEndpoint_returnsCorrectModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/computers").with(user(authenticationMetadata)).flashAttr("user", user).flashAttr("bool", history.isShow());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer"))
                .andExpect(model().attributeExists("computers"));

        verify(computerService, times(1)).getAllComputers(any());

    }

    @Test
    void getUnauthenticatedRequestToComputersEndpoint_returnsBackToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/computers").with(user(unauthenticatedAuthenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(computerService, never()).getAllComputers(any());

    }

    @Test
    void getRequestToComputerAddEndpoint_returnsModelAndView() throws Exception {
        MockHttpServletRequestBuilder request = get("/computers/add").with(user(authenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getUsersWithoutComputer();
    }

    @Test
    void postValidRequestToComputerAddEndpoint_thenAddsComputerAndRedirectsToComputersPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/computers/add")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .formField("age", "0")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/computers?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(computerService, times(1)).addComputer(any(), any());
    }

    @Test
    void postInvalidRequestToComputerAddEndpoint_thenReturnsAddPage() throws Exception {
        MockHttpServletRequestBuilder request = post("/computers/add")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getById(any());
        verify(computerService, never()).addComputer(any(), any());
    }

    @Test
    void getRequestToEditComputerEndpointWithOwnerNotNull_thenReturnsEditPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/computers/edit/{id}", 5).with(user(authenticationMetadata)).flashAttr("user", user);

        Computer computer = Computer.builder().id(5).owner(User.builder().department(UserDepartment.IT).build()).build();
        List<User> userList = new ArrayList<>(List.of(User.builder().department(UserDepartment.IT).build(), User.builder().department(UserDepartment.IT).build()));

        when(userService.getUsersWithoutComputer()).thenReturn(userList);
        when(computerService.getById(5)).thenReturn(computer);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getUsersWithoutComputer();
        verify(computerService, times(1)).getById(5);
        assertEquals(3, userList.size());

    }

    @Test
    void getRequestToEditComputerEndpointWithOwnerNull_thenReturnsEditPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/computers/edit/{id}", 5).with(user(authenticationMetadata)).flashAttr("user", user);
        Computer computer = Computer.builder().id(5).build();

        List<User> userList = new ArrayList<>(List.of(User.builder().department(UserDepartment.IT).build(), User.builder().department(UserDepartment.IT).build()));

        when(userService.getUsersWithoutComputer()).thenReturn(userList);
        when(computerService.getById(5)).thenReturn(computer);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getUsersWithoutComputer();
        verify(computerService, times(1)).getById(5);
        assertEquals(2, userList.size());

    }

    @Test
    void putRequestToEditComputerEndpoint_thenEditComputerAndRedirectToComputersPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/computers/edit/5")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .formField("age", "0")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doNothing().when(computerService).editComputer(anyLong(), any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/computers?show=" + history.isShow()));

        verify(userService, times(1)).getById(any());
        verify(computerService, times(1)).editComputer(anyLong(), any(), any());

    }

    @Test
    void putInvalidRequestToEditComputerWithOwnerEndpoint_thenReturnToEditPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/computers/edit/5")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .with(csrf());

        Computer computer = Computer.builder().id(5).owner(User.builder().department(UserDepartment.IT).build()).build();

        List<User> userList = new ArrayList<>(List.of(User.builder().department(UserDepartment.IT).build(), User.builder().department(UserDepartment.IT).build()));

        when(userService.getUsersWithoutComputer()).thenReturn(userList);
        when(computerService.getById(5)).thenReturn(computer);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getUsersWithoutComputer();
        verify(computerService, times(1)).getById(5);
        assertEquals(3, userList.size());

    }

    @Test
    void putInvalidRequestToEditComputerWithOwnerNullEndpoint_thenReturnToEditPage() throws Exception {
        MockHttpServletRequestBuilder request = put("/computers/edit/5")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .with(csrf());

        Computer computer = Computer.builder().id(5).build();

        List<User> userList = new ArrayList<>(List.of(User.builder().department(UserDepartment.IT).build(), User.builder().department(UserDepartment.IT).build()));

        when(userService.getUsersWithoutComputer()).thenReturn(userList);
        when(computerService.getById(5)).thenReturn(computer);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("computer-edit"))
                .andExpect(model().attributeExists("method", "endpoint", "computerRequest", "freeUsers"));

        verify(userService, times(1)).getUsersWithoutComputer();
        verify(computerService, times(1)).getById(5);
        assertEquals(2, userList.size());

    }

    @Test
    void postDuplicateRequestToComputerAddEndpoint_thenThrowsException() throws Exception {
        MockHttpServletRequestBuilder request = post("/computers/add")
                .with(user(authenticationMetadata))
                .formField("computerName", "testwks0001")
                .formField("barcode", "20250101")
                .formField("age", "0")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        doThrow(ComputerAlreadyExistsException.class).when(computerService).addComputer(any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/computers/add"));

        verify(userService, times(1)).getById(any());
        verify(computerService, times(1)).addComputer(any(), any());
    }


}
