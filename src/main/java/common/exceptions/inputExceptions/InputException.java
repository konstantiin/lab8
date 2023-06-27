package common.exceptions.inputExceptions;

/**
 * parent of all input common.exceptions
 */
public class InputException extends RuntimeException {
    public InputException(Exception e) {
        super(e);
    }

    public InputException() {
        super();
    }

    public InputException(String message) {
        super(message);
    }
}
