package common.exceptions.inputExceptions;

/**
 * indicates that such command does not exist
 */
public class UnknownCommandException extends InputException {

    public UnknownCommandException(String command_not_found) {
        super(command_not_found);
    }
}
