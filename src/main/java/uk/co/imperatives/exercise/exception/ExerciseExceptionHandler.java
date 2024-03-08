package uk.co.imperatives.exercise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExerciseExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ExerciseServiceBadRequestException.class})
    public ResponseEntity<Object> handleValidationException(Exception ex){
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({ExerciseServiceException.class})
    public ResponseEntity<Object> handleServiceException(ExerciseServiceException ex){
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler({ExerciseAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsException(ExerciseServiceException ex){
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({ExerciseNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(ExerciseNotFoundException ex){
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Object> createErrorResponse(HttpStatus httpStatus, String errorMessage) {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", dateFormat.format(new Date()));
        body.put("status", httpStatus);
        body.put("error_message", errorMessage);
        return new ResponseEntity<>(body, httpStatus);
    }
}
