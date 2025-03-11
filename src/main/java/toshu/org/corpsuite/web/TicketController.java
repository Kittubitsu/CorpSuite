package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.service.TicketService;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddTicketRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final UserService userService;
    private final TicketService ticketService;
    private final LogService logService;


    public TicketController(UserService userService, TicketService ticketService, LogService logService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.logService = logService;
    }

    @GetMapping
    public ModelAndView getTicketPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", defaultValue = "false") Boolean show) {

        User user = userService.findById(authenticationMetadata.getUserId());
        ModelAndView mav = new ModelAndView("ticket");
        List<Ticket> ticketList = ticketService.getAllByUser(user, show);

        mav.addObject("tickets", ticketList);
        mav.addObject("bool", show);
        mav.addObject("user", user);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getTicketAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        ModelAndView mav = new ModelAndView("ticket-add");
        mav.addObject("user", user);
        mav.addObject("ticketRequest", AddTicketRequest.builder().requester(user.getCorporateEmail()).build());
        mav.addObject("endpoint", "add");
        mav.addObject("method", "POST");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleTicketAddPage(@Valid @ModelAttribute("ticketRequest") AddTicketRequest ticketRequest, BindingResult result) {

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
        Ticket ticket = ticketService.addTicket(ticketRequest, requesterUser, responsibleUser);

        logService.saveLog(LogRequest.builder()
                .email(requesterUser.getCorporateEmail())
                .action("CREATE")
                .module("Ticket")
                .comment("Ticket created with id [%s]".formatted(ticket.getId()))
                .build());


        return new ModelAndView("redirect:/tickets?show=false");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getTicketEditPage(@PathVariable UUID id) {
        Ticket ticket = ticketService.findById(id);

        ModelAndView mav = new ModelAndView("ticket-add");
        mav.addObject("ticketRequest", DtoMapper.toTicketDto(ticket));
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("method", "PUT");

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleTicketEditPage(@PathVariable UUID id, @Valid @ModelAttribute("ticketRequest") AddTicketRequest ticketRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.findById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("ticket-add");
            mav.addObject("ticketRequest", ticketRequest);
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("method", "PUT");

            return mav;
        }

        ticketService.editTicket(id, ticketRequest);

        logService.saveLog(LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Ticket")
                .comment("Ticket edited with id [%s]".formatted(id))
                .build());

        return new ModelAndView("redirect:/tickets?show=false");
    }
}
