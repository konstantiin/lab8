package common.exceptions.inputExceptions;

/**
 * indicates that enum value is incorrect
 */
public class EnumInputException extends InputException {
    public EnumInputException(Exception e) {
        super(e);
    }

    public EnumInputException(String s) {
        super(s);
    }
}
