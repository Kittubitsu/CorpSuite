package toshu.org.corpsuite.exception;

public class LogServiceConnectionException extends RuntimeException {
    public LogServiceConnectionException(String message) {
        super(message);
    }

    public LogServiceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
