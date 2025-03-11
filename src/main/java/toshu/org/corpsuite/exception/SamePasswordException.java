package toshu.org.corpsuite.exception;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException(String message) {
        super(message);
    }

    public SamePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
