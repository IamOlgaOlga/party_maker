package uk.co.imperatives.exercise.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@AllArgsConstructor
@RestController
public class GuestsController {

    private GuestService guestService;

    /**
     * This method аdd a guest to the guestlist.
     * If there is insufficient space at the specified table, then an error should be thrown.
     * @param name name of a new guest
     * @param guestRequest information about guest: table's number and count of accompanying guests
     * @return
     */
    @PostMapping("/guest_list/{name}")
    public @ResponseBody GuestResponse addGuest(@PathVariable(name = "name") String name, @RequestBody @Valid GuestRequest guestRequest) {
        log.debug("Receive a new POST request to add a new guest.");
        return new GuestResponse(guestService.addGuest(name, guestRequest.getTable(), guestRequest.getAccompanyingGuests()));
    }

    @GetMapping("/guest_list")
    public @ResponseBody GuestListResponse getGuestList() {
        log.debug("Receive a new GET request to provide guest list.");
        List<GuestRequest> guestList = new ArrayList<>();
        guestService.getGuestList().forEach(guest -> guestList.add(new GuestRequest(guest.getName(),
                guest.getTableNumber(), guest.getTotalGuests())));
        return new GuestListResponse(guestList);
    }

    @PutMapping("/guests/{name}")
    public @ResponseBody GuestResponse arrivedGuest(@PathVariable(name = "name") String name, @RequestBody @Valid GuestRequest guestRequest) {
        return new GuestResponse("");
    }
}
