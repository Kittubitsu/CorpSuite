package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.model.TicketType;
import toshu.org.corpsuite.user.model.User;

@Data
@Builder
public class TicketAdd {

    @NotNull
    private String requester;

    @NotNull
    private String responsible;

    @NotNull
    @Size(min = 20, message = "Please write a longer comment")
    private String comment;

    @NotNull
    private TicketStatus status;

    @NotNull
    private TicketType type;
}
