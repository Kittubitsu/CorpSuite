package toshu.org.corpsuite.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;


@Controller()
@RequestMapping("/home")
public class HomeController {

    private final UserService userService;
    private final RequestService requestService;
    private final TicketService ticketService;
    private final CardService cardService;

    public HomeController(UserService userService, RequestService requestService, TicketService ticketService, CardService cardService) {
        this.userService = userService;
        this.requestService = requestService;
        this.ticketService = ticketService;
        this.cardService = cardService;
    }

    @GetMapping
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView mav = new ModelAndView("home");

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Request> requestList = requestService.getFirstThreePendingRequestsByUser(user);
        List<Ticket> ticketList = ticketService.getFirstThreePendingTicketsByUser(user);
        List<Card> allCards = cardService.getAllFreeCards(user);

        mav.addObject("userRequest", DtoMapper.toEditUserDto(user));
        mav.addObject("cards", allCards);
        mav.addObject("requestList", requestList);
        mav.addObject("ticketList", ticketList);
        mav.addObject("method", "PUT");
        mav.addObject("endpoint", "edit/" + user.getId());

        return mav;
    }
}
