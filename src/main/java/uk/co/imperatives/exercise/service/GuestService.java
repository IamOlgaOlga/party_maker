package uk.co.imperatives.exercise.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;
import uk.co.imperatives.exercise.repository.data.Guest;

@Slf4j
@AllArgsConstructor
@Service
public class GuestService {

    private JpaGuestRepository guestRepository;

    /**
     * Save a new guest to DB and update(or save) information about tables.
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
        log.debug("Try to insert new values to tables and guests DB tables");
        guestRepository.updateGuestAndTable(guest);
        String savedName = guestRepository.getGuestName(guest);
        if (savedName == null) {
            var errorMessage = "An error occurs while saving a new guest";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return savedName;
    }
}
