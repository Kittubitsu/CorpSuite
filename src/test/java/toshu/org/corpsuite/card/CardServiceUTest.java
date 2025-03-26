package toshu.org.corpsuite.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.model.CardType;
import toshu.org.corpsuite.card.repository.CardRepository;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.CardAlreadyExistsException;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.CardRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceUTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CardService cardService;

    @Test
    void givenCardAlreadyExists_whenAddingDuplicateCard_thenThrows() {

        //Given
        CardRequest cardRequest = CardRequest.builder()
                .code("15")
                .active(true)
                .type(CardType.IT)
                .build();

        Card card = Card.builder()
                .code("15")
                .build();
        //When

        when(cardRepository.findCardByCode(any())).thenReturn(Optional.of(card));

        //Then

        assertThrows(CardAlreadyExistsException.class, () -> cardService.addCard(cardRequest, new User()));
    }

    @Test
    void givenCard_whenAddingCard_thenCompletesSuccessfully() {

        //Given
        CardRequest cardRequest = CardRequest.builder()
                .code("15")
                .active(true)
                .type(CardType.IT)
                .build();

        Card card = Card.builder()
                .id(1)
                .code("15")
                .active(true)
                .type(CardType.IT)
                .build();
        //When

        when(cardRepository.findCardByCode(any())).thenReturn(Optional.empty());
        when(cardRepository.save(any())).thenReturn(card);
        doNothing().when(applicationEventPublisher).publishEvent(any(LoggingEvent.class));

        cardService.addCard(cardRequest, User.builder().corporateEmail("toshu@abv.bg").build());

        //Then
        verify(cardRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));
    }

    @Test
    void givenMissingCard_whenGettingCardById_thenThrows() {

        //Given
        Long id = 1L;

        //When
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        //Then
        assertThrows(DomainException.class, () -> cardService.getCardById(id));

    }

    @Test
    void givenExistingCard_whenGettingCardById_thenReceiveCard() {

        //Given
        Card card = Card.builder()
                .id(1)
                .type(CardType.IT)
                .active(true)
                .code("15")
                .owner(new User())
                .build();

        //When
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        Card cardById = cardService.getCardById(card.getId());

        //Then
        assertEquals(card.getId(), cardById.getId());
        assertEquals(card.getCode(), cardById.getCode());
        assertEquals(card.getType(), cardById.getType());
        assertEquals(card.getOwner(), cardById.getOwner());
        verify(cardRepository, times(1)).findById(any());
    }

    @Test
    void givenBoolTrue_whenGettingAllCards_thenReturnAll() {
        //Given
        List<Card> expectedList = List.of(Card.builder().active(true).build(), Card.builder().active(false).build());

        //When
        when(cardRepository.findAll()).thenReturn(expectedList);

        List<Card> actualList = cardService.getAllCards(true);

        //Then
        assertEquals(expectedList.size(), actualList.size());
        verify(cardRepository, times(1)).findAll();

    }

    @Test
    void givenBoolFalse_whenGettingAllCards_thenReturnAllWithActiveStatus() {
        //Given
        List<Card> expectedList = List.of(Card.builder().active(true).build(), Card.builder().active(false).build());

        //When
        when(cardRepository.findAll()).thenReturn(expectedList);

        List<Card> actualList = cardService.getAllCards(false);

        //Then
        assertNotEquals(expectedList.size(), actualList.size());
        verify(cardRepository, times(1)).findAll();

    }

    @Test
    void givenAllFreeCards_whenGettingAllFreeCards_ReturnsSuccessfully() {

        //Given
        List<Card> expectedList = List.of(Card.builder().active(true).build(), Card.builder().active(true).build());

        //When
        when(cardRepository.findAllByOwnerIsNullAndActive()).thenReturn(expectedList);

        User user = User.builder().build();

        List<Card> actualList = cardService.getAllFreeCards(user);
        //Then
        assertEquals(expectedList.size(), actualList.size());
        verify(cardRepository, times(1)).findAllByOwnerIsNullAndActive();

    }

    @Test
    void givenAllFreeCardsAndUserHasCard_whenGettingAllFreeCards_ReturnsSuccessfullyAllCardsIncludingUserCard() {

        //Given
        List<Card> expectedList = new ArrayList<>(List.of(Card.builder().active(true).build(), Card.builder().active(true).build()));

        //When
        when(cardRepository.findAllByOwnerIsNullAndActive()).thenReturn(expectedList);

        User user = User.builder().card(Card.builder().build()).build();

        List<Card> actualList = cardService.getAllFreeCards(user);
        //Then
        assertEquals(expectedList.size(), actualList.size());
        verify(cardRepository, times(1)).findAllByOwnerIsNullAndActive();

    }

    @Test
    void givenExistingCard_whenEditCard_thenCardIsEdited() {

        //Given
        CardRequest cardRequest = CardRequest.builder()
                .type(CardType.IT)
                .code("15")
                .active(true)
                .build();

        Card card = Card.builder()
                .active(false)
                .id(1)
                .code("12")
                .type(CardType.HR)
                .build();

        //When
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);

        cardService.editCard(cardRequest, card.getId(), new User());

        //Then
        assertEquals(cardRequest.getType(), card.getType());
        assertEquals(cardRequest.getCode(), card.getCode());
        assertEquals(cardRequest.getActive(), card.isActive());
        verify(cardRepository, times(1)).save(card);
        verify(applicationEventPublisher, times(1)).publishEvent(any(LoggingEvent.class));

    }
}
