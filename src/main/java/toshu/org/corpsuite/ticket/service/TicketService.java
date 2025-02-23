package toshu.org.corpsuite.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.repository.TicketRepository;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.web.dto.TicketAdd;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket addTicket(TicketAdd ticketAdd, User requester, User responsible) {

        Ticket ticket = Ticket.builder()
                .requester(requester)
                .responsible(responsible)
                .comment(ticketAdd.getComment())
                .comment(ticketAdd.getComment())
                .status(ticketAdd.getStatus())
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {

        return ticketRepository.findAll();
    }

    public List<Ticket> getAllByUser(User user) {

        return ticketRepository.findAllByRequesterOrResponsible(user, user);
    }
}
