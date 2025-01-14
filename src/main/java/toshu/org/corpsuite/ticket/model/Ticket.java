package toshu.org.corpsuite.ticket.model;

import jakarta.persistence.*;
import toshu.org.corpsuite.user.model.User;

import java.util.UUID;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User responsible;

    @Column(nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
}
