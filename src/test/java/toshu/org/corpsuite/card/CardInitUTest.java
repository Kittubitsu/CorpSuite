package toshu.org.corpsuite.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardInit;
import toshu.org.corpsuite.card.service.CardService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CardInitUTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardInit cardInit;


    @Test
    void givenNoCards_whenRun_thenCreateFirstCards() throws Exception {

        when(cardService.getAllCards(true)).thenReturn(Collections.emptyList());

        cardInit.run();

        verify(cardService, atLeastOnce()).addCard(any(), any());
    }

    @Test
    void givenCardsExists_whenRun_thenDoNothing() throws Exception {

        when(cardService.getAllCards(true)).thenReturn(List.of(new Card()));

        cardInit.run();

        verify(cardService, never()).addCard(any(), any());
    }

}
