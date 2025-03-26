package toshu.org.corpsuite.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.repository.TicketRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.TicketRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public TicketService(TicketRepository ticketRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.ticketRepository = ticketRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void addTicket(TicketRequest ticketRequest, User requester, User responsible) {

        Ticket ticket = Ticket.builder()
                .requester(requester)
                .responsible(responsible)
                .comment(ticketRequest.getComment())
                .type(ticketRequest.getType())
                .status(ticketRequest.getStatus())
                .opened(LocalDateTime.now())
                .build();

        Ticket saved = ticketRepository.save(ticket);

        LogRequest logRequest = LogRequest.builder()
                .email(requester.getCorporateEmail())
                .action("CREATE")
                .module("Ticket")
                .comment("Ticket created with id [%s]".formatted(saved.getId()))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));

    }

    public List<Ticket> getAllByUser(User user, boolean show) {
        List<Ticket> ticketList = ticketRepository.findAllByRequesterOrResponsible(user, user);
        if (!show) {
            return ticketList.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.PENDING)).toList();
        }

        return ticketList;
    }

    public Ticket getById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new DomainException("Ticket with this ID does not exist!"));
    }

    public void editTicket(UUID id, TicketRequest ticketRequest, User user) {
        Ticket ticket = getById(id);
        ticket.setType(ticketRequest.getType());
        ticket.setComment(ticketRequest.getComment());
        ticket.setStatus(ticketRequest.getStatus());

        if (ticket.getStatus().equals(TicketStatus.COMPLETED) || ticket.getStatus().equals(TicketStatus.REJECTED)) {
            ticket.setClosed(LocalDateTime.now());
        }

        ticketRepository.save(ticket);

        LogRequest logRequest = LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("Ticket")
                .comment("Ticket edited with id [%s]".formatted(id))
                .build();

        applicationEventPublisher.publishEvent(new LoggingEvent(logRequest));
    }


    public List<Ticket> getFirstThreePendingTicketsByUser(User user) {
        return ticketRepository.findAllByRequesterOrResponsible(user, user).stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.PENDING)).limit(3).toList();
    }
}
