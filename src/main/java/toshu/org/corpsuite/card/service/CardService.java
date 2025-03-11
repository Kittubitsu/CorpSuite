package toshu.org.corpsuite.card.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.repository.CardRepository;
import toshu.org.corpsuite.exception.CardAlreadyExistsException;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.web.dto.AddCardRequest;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card addCard(AddCardRequest cardRequest) {

        if (cardRepository.findCardByCode(cardRequest.getCode()).isPresent()) {
            throw new CardAlreadyExistsException("Card with this code exists already!");
        }

        Card card = Card.builder()
                .code(cardRequest.getCode())
                .isActive(cardRequest.getIsActive())
                .type(cardRequest.getType())
                .build();


        return cardRepository.save(card);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new DomainException("Card not found!"));
    }

    public List<Card> getAllCards(Boolean show) {
        List<Card> cards = cardRepository.findAll();
        if (!show) {
            return cards.stream().filter(Card::isActive).toList();
        }
        return cards;
    }

    public List<Card> getAllFreeCards() {

        return cardRepository.findAllByOwnerIsNullAndActive();
    }

    public List<Card> getAllActiveCards() {
        return cardRepository.findAllActiveCards();
    }

    public void editCard(AddCardRequest cardRequest, Long id) {

        Card card = getCardById(id);
        card.setCode(cardRequest.getCode());
        card.setType(cardRequest.getType());
        card.setCode(cardRequest.getCode());
        card.setActive(cardRequest.getIsActive());

        cardRepository.save(card);
    }
}
