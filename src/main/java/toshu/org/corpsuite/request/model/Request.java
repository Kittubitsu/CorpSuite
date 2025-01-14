package toshu.org.corpsuite.request.model;

import jakarta.persistence.*;
import toshu.org.corpsuite.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User responsible;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    private String comment;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

}
