package toshu.org.corpsuite.log.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Log {

    private String email;

    private String action;

    private String module;

    private String comment;

    private LocalDateTime timestamp;

}
