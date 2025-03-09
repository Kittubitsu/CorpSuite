package toshu.org.corpsuite.log.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import toshu.org.corpsuite.exception.DomainException;
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
            throw new DomainException("Cannot get list of logs!");
        }

        return listResponseEntity.getBody();
    }

    public void saveLog(LogRequest logRequest) {
        ResponseEntity<Void> response = logClient.saveLog(logRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DomainException("Could not communicate with log service!");
        }
    }
}
