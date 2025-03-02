package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.TicketAdd;
import toshu.org.corpsuite.web.mapper.dtoMapper;

import java.util.List;
import java.util.UUID;

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
        mav.addObject("endpoint", "add");
        mav.addObject("method", "POST");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleTicketAddPage(@Valid @ModelAttribute("ticketRequest") TicketAdd ticketRequest, BindingResult result) {

        User requesterUser = userService.findByEmail(ticketRequest.getRequester());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("ticket-add");
            mav.addObject("user", requesterUser);
            mav.addObject("ticketRequest", ticketRequest);
            return mav;
        }
        User responsibleUser = userService.getRandomUserFromDepartment(ticketRequest.getDepartment());
        ticketService.addTicket(ticketRequest, requesterUser, responsibleUser);

        return new ModelAndView("redirect:/tickets");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getTicketEditPage(@PathVariable UUID id) {
        Ticket ticket = ticketService.findById(id);

        ModelAndView mav = new ModelAndView("ticket-add");
        mav.addObject("ticketRequest", dtoMapper.toTicketDto(ticket));
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("method", "PUT");

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleTicketEditPage(@PathVariable UUID id, @Valid @ModelAttribute("ticketRequest") TicketAdd ticketRequest, BindingResult result) {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("ticket-add");
            mav.addObject("ticketRequest", ticketRequest);
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("method", "PUT");

            return mav;
        }
        ticketService.editTicket(id,ticketRequest);

        return new ModelAndView("redirect:/tickets");
    }
}
