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
     *
     * @param name               new guest name
     * @param tableNumber        table number for reservation
     * @param accompanyingGuests main guest's accompanying guests
     * @return guest name in case success processing.
     */
    public String addGuest(String name, int tableNumber, int accompanyingGuests) {
        // We always store a total number of guests
        var guest = new Guest(name, tableNumber, accompanyingGuests + 1);
        // Check if the guest already exists
        if (guestRepository.exists(guest)) {
            var errorMessage = String.format("Guest with name %s already exists", guest.getName());
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        // Check if the table exists
        if (!tableRepository.exists(tableNumber)) {
            var errorMessage = String.format("There is no table with ID = %d", tableNumber);
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }

        var rowsAffected = guestRepository.saveGuest(guest);
        if (rowsAffected == 0) {
            var errorMessage = String.format("There is no free space at the table with ID = %d", tableNumber);
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        return name;
    }

    /**
     * Method provides a guests list who booked tables.
     *
     * @return a guests list.
     */
    public List<Guest> getGuestList() {
        log.debug("Call DB to get guest list");
        return guestRepository.getGuestList();
    }

    /**
     * Method checks availability of the table's space for arrived guests and return the guest's name in case success.
     * Throw an exception in case unavailable space at the table.
     *
     * @param name               main guest's name
     * @param accompanyingGuests main guest's accompanying friends.
     * @return main guest's name or throw an axception.
     */
    public String checkInGuest(String name, int accompanyingGuests) {
        return "";
    }
}
