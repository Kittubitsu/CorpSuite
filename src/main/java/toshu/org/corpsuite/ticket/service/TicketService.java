package toshu.org.corpsuite.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.repository.TicketRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.AddTicketRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket addTicket(AddTicketRequest addTicketRequest, User requester, User responsible) {

        Ticket ticket = Ticket.builder()
                .requester(requester)
                .responsible(responsible)
                .comment(addTicketRequest.getComment())
                .type(addTicketRequest.getType())
                .status(addTicketRequest.getStatus())
                .opened(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {

        return ticketRepository.findAll();
    }

    public List<Ticket> getAllByUser(User user, boolean show) {
        List<Ticket> ticketList = ticketRepository.findAllByRequesterOrResponsible(user, user);
        if (!show) {
            return ticketList.stream().filter(ticket -> ticket.getStatus().equals(TicketStatus.PENDING)).toList();
        }

        return ticketList;
    }

    public Ticket findById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new DomainException("Ticket with this ID does not exist!"));
    }

    public void editTicket(UUID id, AddTicketRequest ticketRequest) {
        Ticket ticket = findById(id);
        ticket.setType(ticketRequest.getType());
        ticket.setComment(ticketRequest.getComment());
        ticket.setStatus(ticketRequest.getStatus());
        if (ticket.getStatus().equals(TicketStatus.COMPLETED)) {
            ticket.setClosed(LocalDateTime.now());
        }
        ticketRepository.save(ticket);
    }

}
