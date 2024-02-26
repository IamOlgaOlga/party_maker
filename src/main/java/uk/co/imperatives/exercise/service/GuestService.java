package uk.co.imperatives.exercise.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;
import uk.co.imperatives.exercise.repository.JpaTableRepository;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class GuestService {

    private JpaGuestRepository guestRepository;

    private JpaTableRepository tableRepository;

    /**
     * Save a new guest to DB in case if there are seats available for specific table.
     * @param name new guest name
     * @param tableNumber table number for reservation
     * @param accompanyingGuests main guest's accompanying guests
     * @return guest name in case success processing.
     */
    public String addGuest(String name, int tableNumber, int accompanyingGuests){
        Guest guest = new Guest();
        guest.setName(name);
        guest.setTableNumber(tableNumber);
        guest.setAccompanyingGuests(accompanyingGuests);
        // Check if the guest already exists
        if (guestRepository.getGuestName(guest) != null) {
            var errorMessage = "Guest with name " + guest.getName() + "already exists";
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        // Check available seats
        if (accompanyingGuests + 1 > tableRepository.getTableAvailableSeats(tableNumber)) {
            var errorMessage = "There is no available seats for table ID = " + tableNumber;
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        log.debug("Try to insert new values to guests DB table");
        guestRepository.saveGuest(guest);
        var savedName = guestRepository.getGuestName(guest);
        if (savedName == null) {
            var errorMessage = "An error occurs while saving a new guest";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return savedName;
    }

    public List<Guest> getGuestList() {
        log.debug("Call DB to get guest list");
        return guestRepository.getGuestList();
    }
}
