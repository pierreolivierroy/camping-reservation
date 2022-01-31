package upgrade.challenge.availability.v1.v1.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import upgrade.challenge.availability.exception.NotFoundException;

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
                .message("Invalid field(s) provided")
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ErrorMessage missingServletRequestParameterExceptionHandler(final MissingServletRequestParameterException exception) {
        return ErrorMessage.builder()
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    public ErrorMessage notFoundExceptionHandler(final NotFoundException exception) {
        return ErrorMessage.builder()
                .message(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public ErrorMessage internalServerErrorExceptionHandler() {
        return ErrorMessage.builder()
                .message("Internal server error")
                .build();
    }
}
