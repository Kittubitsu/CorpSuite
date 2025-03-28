package toshu.org.corpsuite.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.CardRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;

@Controller
@RequestMapping("/cards")
@PreAuthorize("hasAnyRole('HR','ADMIN')")
public class CardController {

    private final UserService userService;
    private final CardService cardService;
    private final History history;

    public CardController(UserService userService, CardService cardService, History history) {
        this.userService = userService;
        this.cardService = cardService;
        this.history = history;
    }

    @GetMapping
    public ModelAndView getCards(@RequestParam(name = "show", required = false, defaultValue = "false") Boolean show) {
        ModelAndView mav = new ModelAndView("card");

        history.setShow(show);
        List<Card> cards = cardService.getAllCards(show);

        mav.addObject("cards", cards);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getAddCardPage() {
        ModelAndView mav = new ModelAndView("card-edit");

        mav.addObject("cardRequest", CardRequest.builder().build());
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleAddCardPage(CardRequest cardRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        cardService.addCard(cardRequest, user);

        return new ModelAndView("redirect:/cards?show=" + history.isShow());
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditCardPage(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("card-edit");

        Card card = cardService.getCardById(id);

        mav.addObject("cardRequest", DtoMapper.toCardDto(card));
        mav.addObject("method", "PUT");
        mav.addObject("endpoint", "edit/" + card.getId());

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleEditCardPage(CardRequest cardRequest, @PathVariable Long id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        cardService.editCard(cardRequest, id, user);

        return new ModelAndView("redirect:/cards?show=" + history.isShow());
    }
}
