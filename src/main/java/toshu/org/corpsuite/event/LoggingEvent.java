package toshu.org.corpsuite.event;

import lombok.Getter;
import toshu.org.corpsuite.log.client.dto.LogRequest;


@Getter
public class LoggingEvent{

    private final LogRequest logRequest;

    public LoggingEvent(LogRequest logRequest) {
        this.logRequest = logRequest;
    }
}
