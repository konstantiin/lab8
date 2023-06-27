package common.exceptions.inputExceptions;

/**
 * indicates that value is out of bounds
 */
public class OutOfBoundsException extends InputException {

    public OutOfBoundsException(String s) {
        super(s);
    }

    public OutOfBoundsException() {
        super();
    }
}
