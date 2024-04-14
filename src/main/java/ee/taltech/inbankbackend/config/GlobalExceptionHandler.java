package ee.taltech.inbankbackend.config;

import ee.taltech.inbankbackend.ErrorHandler.ErrorResponse;
import ee.taltech.inbankbackend.ErrorHandler.ErrorResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleErrorResponseException(ErrorResponseException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorResponse());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Exception ex) {
        return ErrorResponse.INTERNAL_SERVER_ERROR;
    }
}
