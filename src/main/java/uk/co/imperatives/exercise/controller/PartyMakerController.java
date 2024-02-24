package uk.co.imperatives.exercise.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.dto.GuestResponse;
import uk.co.imperatives.exercise.service.GuestService;

@Slf4j
@AllArgsConstructor
@RestController
public class PartyMakerController {

    private GuestService guestService;

    @PostMapping("/guest_list/{name}")
    public @ResponseBody GuestResponse addGuest(@PathVariable(name = "name") String name, @RequestBody GuestRequest guestRequest){
        log.debug("Receive a new POST request to add a new guest.");
        return new GuestResponse(guestService.addGuest(name, guestRequest.getTable(), guestRequest.getAccompanyingGuests()));
    }
}
