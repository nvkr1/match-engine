package bbattulga.matchengine.servicematchengine.config;

import bbattulga.matchengine.libmodel.dto.response.ErrorResponse;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            BadParameterException.class,
    })
    public ResponseEntity<Object> handleBadParameter(
            RuntimeException ex, WebRequest request) {
        final var responseBody = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, responseBody,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            ServiceUnavailableException.class,
    })
    public ResponseEntity<Object> handleServiceUnavailable(
            RuntimeException ex, WebRequest request) {
        final var responseBody = ErrorResponse.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, responseBody,
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

}
