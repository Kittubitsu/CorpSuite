package toshu.org.corpsuite.card.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.repository.CardRepository;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.web.dto.CardAdd;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card addCard(CardAdd cardRequest) {

        if (cardRepository.findCardByCode(cardRequest.getCode()).isPresent()) {
            throw new DomainException("Card with this code exists already!");
        }

        Card card = Card.builder()
                .code(cardRequest.getCode())
                .isActive(cardRequest.isActive())
                .type(cardRequest.getType())
                .build();


        return cardRepository.save(card);
    }

    public List<Card> getAllCards() {

        return cardRepository.findAll();
    }

    public List<Card> getAllFreeCards() {

        return cardRepository.findAllByOwnerIsNullAndActive();
    }
}
