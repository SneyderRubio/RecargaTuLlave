package co.tullave.rcg.exception;

import co.tullave.rcg.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class TlvErrorAdvisor {

    private static final Logger log = LoggerFactory.getLogger(TlvErrorAdvisor.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        log.warn("Resource not found on path {}: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed on path {}. Fields: {}", request.getRequestURI(), fieldErrors.keySet());

        return ResponseEntity.badRequest().body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        log.warn("Invalid request body on path {}", request.getRequestURI());

        return ResponseEntity.badRequest().body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid request body. paymentMethod must be one of: PSE, NEQUI, DAVIPLATA, CREDIT_CARD",
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error on path {}", request.getRequestURI(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI(),
                null
        ));
    }
}
