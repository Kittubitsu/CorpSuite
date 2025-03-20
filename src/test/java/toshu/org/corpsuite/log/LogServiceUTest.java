package toshu.org.corpsuite.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import toshu.org.corpsuite.event.LoggingEvent;
import toshu.org.corpsuite.exception.LogServiceConnectionException;
import toshu.org.corpsuite.log.client.LogClient;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.log.service.LogService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceUTest {

    @Mock
    private LogClient logClient;

    @InjectMocks
    private LogService logService;

    @Test
    void givenLogs_whenGettingAllLogs_thenReturnSuccessfully() {

        List<Log> logs = List.of(Log.builder().build(), Log.builder().build(), Log.builder().build());

        when(logClient.getAllLogs()).thenReturn(ResponseEntity.of(Optional.of(logs)));

        List<Log> allLogs = logService.getAllLogs();

        assertEquals(logs.size(), allLogs.size());
        verify(logClient, times(1)).getAllLogs();

    }

    @Test
    void givenNoConnectionToLogsService_whenGettingAllLogs_thenThrows() {

        when(logClient.getAllLogs()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(LogServiceConnectionException.class, () -> logService.getAllLogs());
        verify(logClient, times(1)).getAllLogs();

    }

    @Test
    void givenLogEvent_whenSavingLog_thenCompleteSuccessfully() {
        LoggingEvent loggingEvent = new LoggingEvent(LogRequest.builder().build());

        when(logClient.saveLog(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertDoesNotThrow(() -> logService.saveLogEvent(loggingEvent));

        verify(logClient, times(1)).saveLog(any());
    }

    @Test
    void givenNoConnectionToService_whenSavingLogs_thenThrows() {
        LoggingEvent loggingEvent = new LoggingEvent(LogRequest.builder().build());

        when(logClient.saveLog(any())).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(LogServiceConnectionException.class, () -> logService.saveLogEvent(loggingEvent));

        verify(logClient, times(1)).saveLog(any());
    }

    @Test
    void givenLogsInDatabase_whenDeletingLog_thenCompleteSuccessfully() {

        when(logClient.deleteAllLogs()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertDoesNotThrow(() -> logService.deleteAllLogs());

        verify(logClient, times(1)).deleteAllLogs();
    }

    @Test
    void givenNoConnectionToService_whenDeletingLogs_thenThrows() {

        when(logClient.deleteAllLogs()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(LogServiceConnectionException.class, () -> logService.deleteAllLogs());

        verify(logClient, times(1)).deleteAllLogs();
    }
}
