package toshu.org.corpsuite.card.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.repository.CardRepository;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.CardAlreadyExistsException;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.AddCardRequest;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public CardService(CardRepository cardRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.cardRepository = cardRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void addCard(AddCardRequest cardRequest, User user) {

        if (cardRepository.findCardByCode(cardRequest.getCode()).isPresent()) {
            throw new CardAlreadyExistsException("Card with this code exists already!");
        }

        Card card = Card.builder()
                .code(cardRequest.getCode())
                .isActive(cardRequest.getIsActive())
                .type(cardRequest.getType())
                .build();

        Card saved = cardRepository.save(card);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("CREATE")
                .module("Card")
                .comment("Card created with id [%d]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
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

    public List<Card> getAllFreeCards(User userToEdit) {
        List<Card> allCards = cardRepository.findAllByOwnerIsNullAndActive();

        if (userToEdit.getCard() != null) {
            allCards.add(userToEdit.getCard());
        }

        return allCards;
    }

    public void editCard(AddCardRequest cardRequest, Long id, User user) {

        Card card = getCardById(id);
        card.setCode(cardRequest.getCode());
        card.setType(cardRequest.getType());
        card.setCode(cardRequest.getCode());
        card.setActive(cardRequest.getIsActive());

        Card saved = cardRepository.save(card);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Card")
                .comment("Card edited with id [%d]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }
}
