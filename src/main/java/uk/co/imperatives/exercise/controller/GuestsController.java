package uk.co.imperatives.exercise.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.imperatives.exercise.dto.GuestListResponse;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.dto.GuestResponse;
import uk.co.imperatives.exercise.service.GuestService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage guests.
 */
@Slf4j
@AllArgsConstructor
@RestController
public class GuestsController {

    private GuestService guestService;

    /**
     * This method аdd a guest to the guests list.
     * If there is insufficient space at the specified table, then an error should be thrown.
     * @param name name of a new guest
     * @param guestRequest information about guest: table's number and count of accompanying guests
     * @return response with guest's name
     */
    @PostMapping("/guest_list/{name}")
    public @ResponseBody GuestResponse addGuest(@PathVariable(name = "name") String name, @RequestBody @Valid GuestRequest guestRequest) {
        log.debug("Receive a new POST request to add a new guest.");
        return new GuestResponse(guestService.addGuest(name, guestRequest.getTable(), guestRequest.getAccompanyingGuests()));
    }

    /**
     * This method provide a list with information about all guests who booked a table for the party.
     * @return a guests list with information about each guest: name, booked table, accompanying guests.
     */
    @GetMapping("/guest_list")
    public @ResponseBody GuestListResponse getGuestList() {
        log.debug("Receive a new GET request to provide guest list.");
        List<GuestRequest> guestList = new ArrayList<>();
        guestService.getGuestList().forEach(guest -> guestList.add(new GuestRequest(guest.getName(),
                guest.getTableNumber(), guest.getTotalGuests() - 1)));
        return new GuestListResponse(guestList);
    }

    /**
     * This method manages an arrived guests.
     * A guest may arrive with his friends that are not the size indicated at the guest list.
     * If the table is expected to have space for the extras, allow them to come.
     * Otherwise, this method should throw an error.
     * @param name name of arrived guest
     * @param guestRequest request with the number of accompanying guests
     * @return response with guest's name
     */
    @PutMapping("/guests/{name}")
    public @ResponseBody GuestResponse arrivedGuest(@PathVariable(name = "name") String name, @RequestBody @Valid GuestRequest guestRequest) {
        log.debug("Receive a new PUT request to check in an arrived guest.");
        return new GuestResponse(guestService.checkInGuest(name, guestRequest.getAccompanyingGuests()));
    }

    /**
     * This method removes guest who leaves the party
     * @param name guest's name who leaves the party.
     * @return response with guest's name
     */
    @DeleteMapping("/guests/{name}")
    public @ResponseBody GuestResponse deleteGuest(@PathVariable(name = "name") String name) {
        log.debug("Receive a new DELETE request to remove guest who leaves the party.");
        return new GuestResponse(guestService.delete(name));
    }
}
