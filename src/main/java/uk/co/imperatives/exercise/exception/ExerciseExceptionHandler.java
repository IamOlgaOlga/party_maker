package uk.co.imperatives.exercise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExerciseExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ExerciseServiceBadRequestException.class})
    public ResponseEntity<Object> handleValidationException(Exception ex){
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Request validation error", ex.getMessage());
    }

    @ExceptionHandler({ExerciseServiceException.class})
    public ResponseEntity<Object> handleValidationException(ExerciseServiceException ex){
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Processing request error", ex.getMessage());
    }

    private ResponseEntity<Object> createErrorResponse(HttpStatus httpStatus, String message, String errorMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", httpStatus);
        body.put("message", message);
        body.put("errors", errorMessage);
        return new ResponseEntity<>(body, httpStatus);
    }
}
