package uk.co.imperatives.exercise.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.dto.GuestResponse;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.service.GuestService;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class PartyMakerController {

    private GuestService guestService;

    @PostMapping("/guest_list/{name}")
    public @ResponseBody GuestResponse addGuest(@PathVariable(name = "name") String name, @RequestBody @Valid GuestRequest guestRequest) {
        log.debug("Receive a new POST request to add a new guest.");
        return new GuestResponse(guestService.addGuest(name, guestRequest.getTable(), guestRequest.getAccompanyingGuests()));
    }

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
