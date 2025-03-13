package toshu.org.corpsuite.log.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.client.dto.LogRequest;

import java.util.List;

@FeignClient(name = "corp-suite-log-svc", url = "http://localhost:8081/api/v1/logs")
public interface LogClient {

    @GetMapping
    ResponseEntity<List<Log>> getAllLogs();

    @PostMapping
    ResponseEntity<Void> saveLog(@RequestBody LogRequest logRequest);

    @DeleteMapping
    ResponseEntity<Void> deleteAllLogs();
}
