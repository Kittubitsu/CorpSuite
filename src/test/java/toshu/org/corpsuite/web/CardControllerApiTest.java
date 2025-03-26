package toshu.org.corpsuite.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.model.CardType;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.exception.CardAlreadyExistsException;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;

import java.util.UUID;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerApiTest {

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

    private AuthenticationMetadata unauthenticatedAuthenticationMetadata;

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

        unauthenticatedAuthenticationMetadata = new AuthenticationMetadata(
                unauthorizedUser.getId(),
                unauthorizedUser.getCorporateEmail(),
                unauthorizedUser.getFirstName(),
                unauthorizedUser.getPassword(),
                unauthorizedUser.getDepartment(),
                unauthorizedUser.isActive());
    }

    @Test
    void getRequestToCardsEndpoint_returnsCardView() throws Exception {

        MockHttpServletRequestBuilder request = get("/cards").with(user(authenticationMetadata)).flashAttr("user", user).flashAttr("bool", history.isShow());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("card"))
                .andExpect(model().attributeExists("cards"));

        verify(cardService, times(1)).getAllCards(any());
    }

    @Test
    void getRequestToCardsAddEndpoint_returnsCardAddView() throws Exception {

        MockHttpServletRequestBuilder request = get("/cards/add").with(user(authenticationMetadata)).flashAttr("user", user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("card-edit"))
                .andExpect(model().attributeExists("cardRequest", "method", "endpoint"));
    }

    @Test
    void postRequestToCardsAddEndpoint_addsToDatabaseAndRedirects() throws Exception {

        MockHttpServletRequestBuilder request = post("/cards/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user)
                .formField("type", CardType.IT.name())
                .formField("code", "1666")
                .formField("active", "true")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cards?show=" + history.isShow()));

        verify(cardService, times(1)).addCard(any(), any());
        verify(userService, times(1)).getById(any());
    }

    @Test
    void getRequestToCardEditEndpoint_returnCardPage() throws Exception {

        Card card = Card.builder()
                .id(5)
                .active(true)
                .type(CardType.IT)
                .code("1542")
                .build();

        MockHttpServletRequestBuilder request = get("/cards/edit/{id}", card.getId()).with(user(authenticationMetadata));

        when(cardService.getCardById(any())).thenReturn(card);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("card-edit"))
                .andExpect(model().attributeExists("cardRequest", "method", "endpoint"));

        verify(cardService, times(1)).getCardById(any());
    }

    @Test
    void putRequestToCardEditEndpoint_editCardAndRedirectToCardsTable() throws Exception {

        Card card = Card.builder()
                .id(5)
                .active(true)
                .type(CardType.IT)
                .code("1542")
                .build();

        MockHttpServletRequestBuilder request = put("/cards/edit/{id}", card.getId()).with(user(authenticationMetadata)).with(csrf());

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cards?show=" + history.isShow()));

        verify(cardService, times(1)).editCard(any(), any(), any());
    }

    @Test
    void getUnauthorizedRequestToCardsPage_returnsBackToHome() throws Exception {
        MockHttpServletRequestBuilder request = get("/cards").with(user(unauthenticatedAuthenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

    }

    @Test
    void postRequestWithDuplicateCard_redirectsBackToFormWithAttribute() throws Exception {
        MockHttpServletRequestBuilder request = post("/cards/add")
                .with(user(authenticationMetadata))
                .flashAttr("user", user)
                .formField("type", CardType.IT.name())
                .formField("code", "1666")
                .formField("active", "true")
                .with(csrf());

        when(userService.getById(any())).thenReturn(user);
        doThrow(CardAlreadyExistsException.class).when(cardService).addCard(any(), any());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cards/add"));

        verify(cardService, times(1)).addCard(any(), any());
        verify(userService, times(1)).getById(any());
    }

    @Test
    void getRequestToCardsPageWithShowParam_shouldShowCardsPageWithCorrectAttribute() throws Exception {
        MockHttpServletRequestBuilder request = get("/cards?show=true").with(user(authenticationMetadata));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attribute("bool", true));
    }

}
