package toshu.org.corpsuite.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.TicketAdd;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;

    public TicketController(UserService userService, TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ModelAndView getTicketPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        List<Ticket> ticketList = ticketService.getAllByUser(user);

        ModelAndView mav = new ModelAndView("ticket");
        mav.addObject("user", user);
        mav.addObject("tickets", ticketList);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getTicketAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        ModelAndView mav = new ModelAndView("ticket-add");
        mav.addObject("user", user);
        mav.addObject("ticketRequest", TicketAdd.builder().requester(user.getCorporateEmail()).build());

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleTicketAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, TicketAdd ticketAddRequest) {

        User responsibleUser = userService.findByEmail(ticketAddRequest.getResponsible());
        User requesterUser = userService.findById(authenticationMetadata.getUserId());

        ticketService.addTicket(ticketAddRequest, requesterUser, responsibleUser);

        return new ModelAndView("redirect:/tickets");
    }
}
