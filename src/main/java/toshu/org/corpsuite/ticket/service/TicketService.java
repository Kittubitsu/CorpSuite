package toshu.org.corpsuite.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.ticket.repository.TicketRepository;
import toshu.org.corpsuite.web.dto.TicketAdd;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket addTicket(TicketAdd ticketAdd){

        Ticket ticket = Ticket.builder()
                .requester(ticketAdd.getRequester())
                .responsible(ticketAdd.getResponsible())
                .comment(ticketAdd.getComment())
                .comment(ticketAdd.getComment())
                .status(ticketAdd.getStatus())
                .build();

        return ticketRepository.save(ticket);
    }
}
