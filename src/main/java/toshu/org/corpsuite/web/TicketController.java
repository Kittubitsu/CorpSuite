package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.TicketRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;
    private final History history;


    public TicketController(UserService userService, TicketService ticketService, History history) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.history = history;
    }

    @GetMapping
    public ModelAndView getTicketPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", required = false, defaultValue = "false") Boolean show) {
        ModelAndView mav = new ModelAndView("ticket");

        history.setShow(show);
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Ticket> ticketList = ticketService.getAllByUser(user, show);

        mav.addObject("tickets", ticketList);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getTicketAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView mav = new ModelAndView("ticket-add");
        mav.addObject("ticketRequest", TicketRequest.builder().requester(user.getCorporateEmail()).build());
        mav.addObject("endpoint", "add");
        mav.addObject("method", "POST");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleTicketAddPage(@Valid @ModelAttribute("ticketRequest") TicketRequest ticketRequest, BindingResult result) {

        User requesterUser = userService.getByEmail(ticketRequest.getRequester());
        User responsibleUser = userService.getRandomUserFromDepartment(ticketRequest.getDepartment());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("ticket-add");
            mav.addObject("user", requesterUser);
            mav.addObject("ticketRequest", ticketRequest);
            mav.addObject("endpoint", "add");
            mav.addObject("method", "POST");
            return mav;
        }

        ticketService.addTicket(ticketRequest, requesterUser, responsibleUser);

        return new ModelAndView("redirect:/tickets?show=" + history.isShow());
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getTicketEditPage(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView("ticket-add");

        Ticket ticket = ticketService.getById(id);

        mav.addObject("ticketRequest", DtoMapper.toTicketDto(ticket));
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("method", "PUT");

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleTicketEditPage(@PathVariable UUID id, @Valid @ModelAttribute("ticketRequest") TicketRequest ticketRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("ticket-add");
            mav.addObject("ticketRequest", ticketRequest);
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("method", "PUT");

            return mav;
        }

        ticketService.editTicket(id, ticketRequest, user);

        return new ModelAndView("redirect:/tickets?show=" + history.isShow());
    }
}
