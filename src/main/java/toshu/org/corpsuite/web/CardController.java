package toshu.org.corpsuite.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.CardAdd;

import java.util.List;

@Controller
@RequestMapping("/cards")
@PreAuthorize("hasAnyRole('HR','ADMIN')")
public class CardController {

    private final UserService userService;
    private final CardService cardService;
    private final LogService logService;
    private Boolean showQuery;

    public CardController(UserService userService, CardService cardService, LogService logService) {
        this.userService = userService;
        this.cardService = cardService;
        this.logService = logService;
    }

    @GetMapping
    public ModelAndView getCards(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", defaultValue = "false") Boolean show) {

        ModelAndView mav = new ModelAndView("card");

        User user = userService.findById(authenticationMetadata.getUserId());
        List<Card> cards = cardService.getAllCards();

        showQuery = show;

        mav.addObject("cards", cards);

        if (!show) {
            List<Card> filteredCards = cards.stream().filter(Card::isActive).toList();
            mav.addObject("cards", filteredCards);
        }

        mav.addObject("bool", show);
        mav.addObject("user", user);

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

    @PostMapping("/add")
    public ModelAndView handleAddCardPage(CardAdd cardRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.findById(authenticationMetadata.getUserId());

        Card card = cardService.addCard(cardRequest);


        logService.saveLog(LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("CREATE")
                .module("Card")
                .comment("Card created with id [%d]".formatted(card.getId()))
                .build());

        return new ModelAndView("redirect:/cards?show=" + showQuery);
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

    @PutMapping("/edit/{id}")
    public ModelAndView handleEditCardPage(CardAdd cardRequest, @PathVariable Long id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        cardService.editCard(cardRequest, id);

        User user = userService.findById(authenticationMetadata.getUserId());

        logService.saveLog(LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Card")
                .comment("Card edited with id [%d]".formatted(id))
                .build());

        return new ModelAndView("redirect:/cards?show=" + showQuery);
    }
}
