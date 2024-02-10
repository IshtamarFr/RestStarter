package fr.ishtamar.starter.exceptionhandler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex));
    }
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentials() {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("These credentials are already used or invalid");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(GenericNotFoundException.class)
    protected ResponseEntity<Object> handleGenericNotFound() {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage("This resource has not been found");
        return buildResponseEntity(apiError);
    }
}
