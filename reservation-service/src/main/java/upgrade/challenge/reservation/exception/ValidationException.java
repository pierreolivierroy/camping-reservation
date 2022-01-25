package upgrade.challenge.reservation.exception;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends RuntimeException {

    private static final String DEFAULT_ERROR_MESSAGE = "Invalid field (s) provided";

    private final List<ValidationError> validationErrors;

    public ValidationException(final List<FieldError> fieldErrors) {
        super(DEFAULT_ERROR_MESSAGE);
        this.validationErrors = initializeValidationErrors(fieldErrors);
    }

    private List<ValidationError> initializeValidationErrors(final List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(this::buildValidationError)
                .collect(Collectors.toList());
    }

    private ValidationError buildValidationError(final FieldError fieldError) {
        return ValidationError.builder()
                .errorMessage(fieldError.getDefaultMessage())
                .rejectedField(fieldError.getField())
                .build();
    }

    @Data
    @Builder
    private static class ValidationError {

        private String errorMessage;
        private String rejectedField;
    }
}
