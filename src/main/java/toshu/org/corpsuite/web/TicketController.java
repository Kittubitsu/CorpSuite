package toshu.org.corpsuite.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.TicketAdd;
import toshu.org.corpsuite.web.mapper.dtoMapper;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;

    private Boolean showQuery;

    public TicketController(UserService userService, TicketService ticketService) {
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ModelAndView getTicketPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", defaultValue = "false") Boolean show) {

        User user = userService.findById(authenticationMetadata.getUserId());
        ModelAndView mav = new ModelAndView("ticket");
        List<Ticket> ticketList = ticketService.getAllByUser(user);

        showQuery = show;

        mav.addObject("tickets", ticketList);
        if (!show) {
            List<Ticket> filteredList = ticketList.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.PENDING)).toList();
            mav.addObject("tickets", filteredList);
        }

        mav.addObject("bool", show);
        mav.addObject("user", user);

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
            mav.addObject("endpoint", "add");
            mav.addObject("method", "POST");
            return mav;
        }
        User responsibleUser = userService.getRandomUserFromDepartment(ticketRequest.getDepartment());
        ticketService.addTicket(ticketRequest, requesterUser, responsibleUser);

        return new ModelAndView("redirect:/tickets?show=" + showQuery);
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

        ticketService.editTicket(id, ticketRequest);

        return new ModelAndView("redirect:/tickets?show=" + showQuery);
    }
}
