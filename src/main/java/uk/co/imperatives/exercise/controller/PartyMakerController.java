package uk.co.imperatives.exercise.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.imperatives.exercise.dto.GuestFullInfoResponse;
import uk.co.imperatives.exercise.dto.GuestListResponse;
import uk.co.imperatives.exercise.dto.NewGuestRequest;
import uk.co.imperatives.exercise.dto.NewGuestResponse;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.data.Guest;
import uk.co.imperatives.exercise.service.GuestService;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class PartyMakerController {

    private GuestService guestService;

    /**
     * This method аdd a guest to the guestlist.
     * If there is insufficient space at the specified table, then an error should be thrown.
     * @param name name of a new guest
     * @param newGuestRequest information about guest: table's number and count of accompanying guests
     * @return
     */
    @PostMapping("/guest_list/{name}")
    public @ResponseBody NewGuestResponse addGuest(@PathVariable(name = "name") String name, @RequestBody @Valid NewGuestRequest newGuestRequest) {
        log.debug("Receive a new POST request to add a new guest.");
        return new NewGuestResponse(guestService.addGuest(name, newGuestRequest.getTable(), newGuestRequest.getAccompanyingGuests()));
    }

    @GetMapping("/guest_list")
    public @ResponseBody GuestListResponse getGuestList() {
        log.debug("Receive a new GET request to provide guest list.");
        List<GuestFullInfoResponse> guestList = new ArrayList<>();
        guestService.getGuestList().forEach(guest -> guestList.add(new GuestFullInfoResponse(guest.getName(),
                guest.getTableNumber(), guest.getAccompanyingGuests())));
        return new GuestListResponse(guestList);
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
