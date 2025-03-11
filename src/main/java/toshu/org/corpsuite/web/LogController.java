package toshu.org.corpsuite.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.service.LogService;

import java.util.List;

@RestController
@RequestMapping("api/v1/logs")
@PreAuthorize("hasAnyRole('IT','ADMIN')")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {

        List<Log> logs = logService.getAllLogs();

        return ResponseEntity.status(HttpStatus.OK).body(logs);
    }
}
