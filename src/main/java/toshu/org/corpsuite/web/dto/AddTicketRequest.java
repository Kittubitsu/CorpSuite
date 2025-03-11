package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import toshu.org.corpsuite.ticket.model.TicketStatus;
import toshu.org.corpsuite.ticket.model.TicketType;
import toshu.org.corpsuite.user.model.UserDepartment;

@Data
@Builder
public class AddTicketRequest {

    private String requester;

    @NotNull(message = "Department cannot be empty!")
    private UserDepartment department;

    @NotEmpty
    @Size(min = 20, message = "Please write a longer comment")
    private String comment;

    @NotNull(message = "Ticket status cannot be empty!")
    private TicketStatus status;

    @NotNull(message = "Ticket type cannot be empty!")
    private TicketType type;
}
