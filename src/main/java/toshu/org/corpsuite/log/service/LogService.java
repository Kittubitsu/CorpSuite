package toshu.org.corpsuite.log.service;

import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.DomainException;
import toshu.org.corpsuite.exception.LogServiceConnectionException;
import toshu.org.corpsuite.log.client.LogClient;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.client.dto.LogRequest;

import java.util.List;

@Service
public class LogService {

    private final LogClient logClient;

    public LogService(LogClient logClient) {
        this.logClient = logClient;
    }

    public List<Log> getAllLogs() {
        ResponseEntity<List<Log>> listResponseEntity = logClient.getAllLogs();

        if (!listResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new LogServiceConnectionException("Cannot get list of logs due to an error in the log service!");
        }

        return listResponseEntity.getBody();
    }

    @Async
    @EventListener
    public void saveLogEvent(LoggingEvent event) {
        LogRequest logRequest = event.getLogRequest();

        ResponseEntity<Void> response = logClient.saveLog(logRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new LogServiceConnectionException("Log cannot be saved, due to an error in the communication with log service!");
        }
    }
}
