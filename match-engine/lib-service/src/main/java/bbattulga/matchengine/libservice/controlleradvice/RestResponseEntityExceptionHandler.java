package bbattulga.matchengine.libservice.controlleradvice;

import bbattulga.matchengine.libmodel.exception.BadParameterException;
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
@RequiredArgsConstructor
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            BadParameterException.class,
    })
    public ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        log.info("handle exception {}", ex.getMessage());
        return handleExceptionInternal(ex, request,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
