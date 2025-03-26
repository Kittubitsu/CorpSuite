package toshu.org.corpsuite.card.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import toshu.org.corpsuite.card.model.CardType;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.CardRequest;

@Component
public class CardInit implements CommandLineRunner {


    private final CardService cardService;

    public CardInit(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (cardService.getAllCards(true).isEmpty()) {
            cardService.addCard(CardRequest.builder().active(true).code("1656").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1657").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1658").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1659").type(CardType.HR).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1660").type(CardType.HR).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1661").type(CardType.HR).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1662").type(CardType.HR).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1663").type(CardType.HR).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1664").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1665").type(CardType.EMPLOYEE).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1666").type(CardType.EMPLOYEE).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1667").type(CardType.EMPLOYEE).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1668").type(CardType.EMPLOYEE).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1669").type(CardType.EMPLOYEE).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1670").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1671").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1672").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1673").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
            cardService.addCard(CardRequest.builder().active(true).code("1674").type(CardType.IT).build(), User.builder().corporateEmail("SYSTEM@CORPSUITE.COM").build());
        }
    }
}
