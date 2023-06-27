package common.exceptions.inputExceptions;

/**
 * indicates that string is empty
 */
public class EmptyStringException extends InputException {
    public EmptyStringException() {
        super();
    }

    public EmptyStringException(String s) {
        super(s);
    }
}
