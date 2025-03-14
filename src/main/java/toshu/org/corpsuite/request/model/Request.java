package toshu.org.corpsuite.request.model;

import jakarta.persistence.*;
import lombok.*;
import toshu.org.corpsuite.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private LocalDate fromDate;

    private LocalDate toDate;

    private int totalDaysOff;

}
