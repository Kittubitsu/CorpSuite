package toshu.org.corpsuite.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.CardAdd;

import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final UserService userService;
    private final CardService cardService;

    public CardController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping
    public ModelAndView getCards(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());
        List<Card> freeCards = cardService.getAllFreeCards();
        ModelAndView mav = new ModelAndView("card");
        mav.addObject("user", user);
        mav.addObject("cards", freeCards);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getAddCardPage() {

        ModelAndView mav = new ModelAndView("card-edit");
        mav.addObject("cardRequest", CardAdd.builder().build());
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");

        return mav;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditCardPage(@PathVariable Long id) {

        Card card = cardService.getCardById(id);

        ModelAndView mav = new ModelAndView("card-edit");
        mav.addObject("cardRequest", CardAdd.builder()
                .code(card.getCode())
                .type(card.getType())
                .isActive(card.isActive())
                .build());

        mav.addObject("method", "PUT");
        mav.addObject("endpoint", "edit/" + card.getId());
        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleAddCardPage(CardAdd cardRequest) {

        cardService.addCard(cardRequest);

        return new ModelAndView("redirect:/cards");
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleEditCardPage(CardAdd cardRequest, @PathVariable Long id) {

        cardService.editCard(cardRequest, id);

        return new ModelAndView("redirect:/cards");
    }
}
