package toshu.org.corpsuite.user.model;

import jakarta.persistence.*;
import lombok.*;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.ticket.model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String corporateEmail;

    private String profilePicture;

    private String country;

    private String department;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserPosition position;

    @Column(nullable = false)
    private boolean isActive;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private LocalDateTime leftOn;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "manager")
    private List<User> subordinates = new ArrayList<>();

    private int paidLeaveCount;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Computer> computers;

    @OneToMany(mappedBy = "requester", fetch = FetchType.EAGER)
    private List<Ticket> openedTickets;

    @OneToMany(mappedBy = "responsible", fetch = FetchType.EAGER)
    private List<Ticket> assignedTickets;

    @OneToMany(mappedBy = "requester", fetch = FetchType.EAGER)
    private List<Request> openedRequests;

    @OneToMany(mappedBy = "responsible", fetch = FetchType.EAGER)
    private List<Request> assignedRequests;

    @OneToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;
}
