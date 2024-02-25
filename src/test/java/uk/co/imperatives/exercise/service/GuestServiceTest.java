package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for GuestService
 */
@SpringBootTest
public class GuestServiceTest {

    @Mock
    private JpaGuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    /**
     * Given guestRepository will return null as guest name first time while checking if guest already exists.
     * Call method addGuest() with test parameters and check a record in guests table one more time.
     * In case of success update repository returns the guest name.
     * Assert that guest service return test guest name.
     */
    @Test
    public void givenGuestInfo_SaveToDbAndReturnName(){
        String guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null).willReturn(guestName);
        assertThat(guestService.addGuest(guestName, 1, 1 ), equalTo(guestName));
    }

    /**
     * Given guestRepository will return the same guest name while checking if a new guest already exists.
     * Assert that exception occurs.
     */
    @Test
    public void givenExistedGuestName_ThrowAnException(){
        var guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(guestName).willReturn(guestName);
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class, () -> guestService.addGuest(guestName, 1, 1 ));
        assertEquals(exception.getMessage(), "Guest with name " + guestName + "already exists");
    }

    /**
     * Given guestRepository will return null as guest name first time while checking if guest already exists.
     * Call method addGuest() with test parameters and check a record in guests table one more time.
     * In case trouble repository returns null.
     * Assert that exception occurs.
     */
    @Test
    public void givenTroubleWhileSavingGuest_ThrowAnException(){
        var guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null).willReturn(null);
        Exception exception = assertThrows(ExerciseServiceException.class, () -> guestService.addGuest(guestName, 1, 1 ));
        assertEquals(exception.getMessage(), "An error occurs while saving a new guest");
    }

    /**
     * Given guestRepository will return a list of guests.
     * Assert that guest service return a list of Guest objects with correct information.
     */
    @Test
    public void givenRequestForGuestList_ReturnTheCorrectListOfGuests(){
        Guest guest1 = new Guest("Jon Snow", 1, 2);
        Guest guest2 = new Guest("Arya Stark", 2, 7);
        List<Guest> guestList = new ArrayList<>(2);
        guestList.add(guest1);
        guestList.add(guest2);
        given(guestRepository.getGuestList()).willReturn(guestList);
        List<Guest> resultList = guestService.getGuestList();
        assertEquals(resultList.size(), 2);
        assertTrue(resultList.stream().anyMatch(guest -> "Jon Snow".equals(guest.getName())
                        && 1 == guest.getTableNumber() && 2 == guest.getAccompanyingGuests()));
        assertTrue(resultList.stream().anyMatch(guest -> "Arya Stark".equals(guest.getName())
                && 2 == guest.getTableNumber() && 7 == guest.getAccompanyingGuests()));
    }
}
