package toshu.org.corpsuite.exception;

public class ComputerAlreadyExistsException extends RuntimeException {
    public ComputerAlreadyExistsException(String message) {
        super(message);
    }

    public ComputerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
