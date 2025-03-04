package toshu.org.corpsuite.web.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import toshu.org.corpsuite.request.model.RequestStatus;
import toshu.org.corpsuite.request.model.RequestType;
import java.time.LocalDate;

@Data
@Builder
public class RequestAdd {

    private String requesterEmail;

    private String responsibleEmail;

    @NotNull(message = "Status cannot be empty!")
    private RequestStatus status;

    @NotNull(message = "Type cannot be empty!")
    private RequestType type;

    @NotEmpty(message = "Comment cannot be empty!")
    private String comment;

    @NotNull(message = "Please select the days for the duration of the absence!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    @NotNull(message = "Please select the days for the duration of the absence!")
    private int totalDays;
}
