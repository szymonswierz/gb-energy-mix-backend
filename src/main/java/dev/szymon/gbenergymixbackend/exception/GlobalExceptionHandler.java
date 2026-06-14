package dev.szymon.gbenergymixbackend.exception;

import dev.szymon.gbenergymixbackend.exception.dto.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalApi(ExternalApiException e) {

        return ResponseEntity.status(502).body(new ApiErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InsufficientDataException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientDataException(InsufficientDataException e) {

        return ResponseEntity.badRequest().body(new ApiErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidHoursException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidHoursException(InvalidHoursException e) {

        return ResponseEntity.status(422).body(new ApiErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

}
