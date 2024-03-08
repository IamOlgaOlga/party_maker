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
        log.debug(String.format("Create a new guest entity for DB: name = %s, table ID = %d, total guests = %d",
                guest.getName(), guest.getTableNumber(), guest.getTotalGuests()));
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
        log.debug(String.format("Add a new guest (%s, %d, %d) to guest list.", name, tableNumber, accompanyingGuests));
        return name;
    }

    /**
     * Method provides a guests list who booked tables.
     *
     * @return a guests list.
     */
    public List<Guest> getGuestList() {
        log.debug("Call DB to get guests list");
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
        // We always store a total number of guests
        var guest = new Guest(name, null, accompanyingGuests + 1);
        log.debug(String.format("A new guest arrived. Create an entity for DB: name = %s, total guests = %d",
                guest.getName(), guest.getTotalGuests()));
        // Check if the guest booked a table
        if (!guestRepository.exists(guest)) {
            var errorMessage = String.format("Guest with name %s did not book a table", guest.getName());
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        var rowsAffected = guestRepository.updateArrivedGuest(guest);
        if (rowsAffected == 0) {
            var errorMessage = String.format("Booked table does not have available space for %d people (main guest name is %s)",
                    guest.getTotalGuests(), name);
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        return name;
    }

    /**
     * This method removes guest who leaves the party.
     * @param name guest's name who leaves a party.
     * @return guest's name in case successful removing from DB, else throw an exception.
     */
    public String delete(String name) {
        var guest = new Guest(name);
        log.debug(String.format("Start removing process for the guest with name = %s", name));
        if (!guestRepository.arrived(guest)) {
            var errorMessage = String.format("Guest with name %s did not arrive to the party", guest.getName());
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        var rowsAffected = guestRepository.deleteGuest(guest);
        if (rowsAffected == 0) {
            var errorMessage = String.format("Some errors occurs while removing the guest with name = %s", name);
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return name;
    }

    /**
     * Method provides a guests list who arrived to the party.
     *
     * @return an arrived guests list.
     */
    public List<Guest> getArrivedGuestList() {
        log.debug("Call DB to get arrived guests list");
        return guestRepository.getArrivedGuestList();
    }

    /**
     * Method returns a count of available seats
     * @return count of available seats
     */
    public int getAvailableSeats() {
        log.debug("Call DB to get available seats");
        return tableRepository.getAvailableSeats();
    }
}
