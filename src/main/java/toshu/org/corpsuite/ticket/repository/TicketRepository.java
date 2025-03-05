package toshu.org.corpsuite.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import toshu.org.corpsuite.ticket.model.Ticket;
import toshu.org.corpsuite.user.model.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findAllByRequesterOrResponsible(User requester, User responsible);

}
