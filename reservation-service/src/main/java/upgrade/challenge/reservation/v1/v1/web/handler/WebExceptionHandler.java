package upgrade.challenge.reservation.v1.v1.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ErrorMessage httpMessageNotReadableExceptionHandler() {
        return ErrorMessage.builder()
                .message("Request body is missing")
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ErrorMessage httpMessageNotReadableExceptionHandler(final MethodArgumentNotValidException exception) {
        final List<String> causeMessages = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ErrorMessage.builder()
                .causes(causeMessages)
                .message("Invalid field provided")
                .build();
    }
}
