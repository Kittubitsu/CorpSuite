package toshu.org.corpsuite.log.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class LogRequest {

    private String email;

    private String action;

    private String module;

    private String comment;
}
