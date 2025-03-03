package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import toshu.org.corpsuite.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestAdd {

    private String requester;

    private String responsible;

    @NotNull
    private RequestStatus status;

    @NotNull
    private RequestType type;

    @NotEmpty
    private String comment;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate fromDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDate toDate;

    private Integer totalDays;
}
