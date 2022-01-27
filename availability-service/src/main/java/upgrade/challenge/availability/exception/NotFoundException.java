package upgrade.challenge.availability.exception;

public class NotFoundException extends RuntimeException {

    private static final String DEFAULT_ERROR_MESSAGE = "Item was not found.";

    public NotFoundException() {
        super(DEFAULT_ERROR_MESSAGE);
    }
}
