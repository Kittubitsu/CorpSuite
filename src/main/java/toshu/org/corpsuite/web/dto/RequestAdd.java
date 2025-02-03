package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import toshu.org.corpsuite.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestAdd {

    @NotNull
    private User requester;

    @NotNull
    private User responsible;

    @NotNull
    private RequestStatus status;

    @NotNull
    private RequestType type;

    @NotNull
    private String comment;

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;
}
