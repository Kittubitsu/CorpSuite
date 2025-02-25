package toshu.org.corpsuite.card.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.model.CardType;
import toshu.org.corpsuite.card.repository.CardRepository;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.web.dto.CardAdd;

import java.util.List;
import java.util.Optional;

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
                .isActive(cardRequest.getIsActive())
                .type(cardRequest.getType())
                .build();


        return cardRepository.save(card);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new DomainException("Card not found!"));
    }

    public List<Card> getAllCards() {

        return cardRepository.findAll();
    }

    public List<Card> getAllFreeCards() {

        return cardRepository.findAllByOwnerIsNullAndActive();
    }

    public void editCard(CardAdd cardRequest, Long id) {

        Card card = getCardById(id);
        card.setCode(cardRequest.getCode());
        card.setType(cardRequest.getType());
        card.setCode(cardRequest.getCode());
        card.setActive(cardRequest.getIsActive());

        cardRepository.save(card);
    }
}
