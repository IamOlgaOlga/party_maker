package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;
import uk.co.imperatives.exercise.repository.JpaTableRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for GuestService
 */
@SpringBootTest
public class GuestServiceTest {

    @Mock
    private JpaGuestRepository guestRepository;

    @Mock
    private JpaTableRepository tableRepository;

    @InjectMocks
    private GuestService guestService;

    /**
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 2.
     * //
     * The guestRepository will return null as guest name first time while check that guest doesn't exist.
     * The tableRepository will return 2 available seats for table with ID = 1.
     * Method saveGuest() must be called.
     * The guestRepository will return guest name second time while check that guest was saved.
     * Output: correct guest name.
     */
    @Test
    public void givenGuestInfo_AvailableSeats_SaveToDbAndReturnName(){
        String guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null).willReturn(guestName);
        given(tableRepository.getTableAvailableSeats(1)).willReturn(2);
        //
        assertThat(guestService.addGuest(guestName, 1, 1 ), equalTo(guestName));
        verify(guestRepository, times(2)).getGuestName(any(Guest.class));
        verify(tableRepository, times(1)).getTableId(any());
        verify(guestRepository, times(1)).saveGuest(any(Guest.class));
    }

    /**
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 1.
     * //
     * The guestRepository will return null as guest name first time while check that guest doesn't exist.
     * The tableRepository will return 2 available seats for table with ID = 1.
     * Method saveGuest() must NOT be called.
     * Output: an ExerciseServiceBadRequestException exception was thrown.
     */
    @Test
    public void givenGuestInfo_NotAvailableSeats_ThrowException(){
        String guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null).willReturn(guestName);
        given(tableRepository.getTableAvailableSeats(1)).willReturn(1);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class, () -> guestService.addGuest(guestName, 1, 1 ));
        assertEquals(exception.getMessage(), "There is no available seats for table ID = 1");
        verify(guestRepository, times(1)).getGuestName(any(Guest.class));
        verify(tableRepository, times(1)).getTableId(any());
        verify(guestRepository, times(0)).saveGuest(any(Guest.class));
    }

    /**
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1.
     * //
     * The guestRepository will return a guest name first time while check that guest doesn't exist.
     * The tableRepository must not be called
     * Method saveGuest() must not be called.
     * The guestRepository must not be called the second time.
     * Output: an ExerciseServiceBadRequestException exception was thrown.
     */
    @Test
    public void givenExistedGuestName_ThrowAnException(){
        var guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(guestName);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class, () -> guestService.addGuest(guestName, 1, 1 ));
        assertEquals(exception.getMessage(), "Guest with name " + guestName + "already exists");
        verify(guestRepository, times(1)).getGuestName(any(Guest.class));
        verify(tableRepository, times(0)).getTableId(any());
        verify(guestRepository, times(0)).saveGuest(any(Guest.class));
    }

    /**
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 2.
     * //
     * The guestRepository will return null as guest name first time while check that guest doesn't exist.
     * The tableRepository will return 2 available seats for table with ID = 1.
     * Method saveGuest() must be called.
     * The guestRepository will return null as guest name the second time.
     * Output: an ExerciseServiceException exception was thrown.
     */
    @Test
    public void givenTroubleWhileSavingGuest_ThrowAnException(){
        var guestName = "Guest Name";
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null).willReturn(null);
        given(tableRepository.getTableAvailableSeats(1)).willReturn(2);
        //
        Exception exception = assertThrows(ExerciseServiceException.class, () -> guestService.addGuest(guestName, 1, 1 ));
        assertEquals(exception.getMessage(), "An error occurs while saving a new guest");
        verify(guestRepository, times(2)).getGuestName(any(Guest.class));
        verify(tableRepository, times(1)).getTableId(any());
        verify(guestRepository, times(1)).saveGuest(any(Guest.class));
    }

    /**
     * Input:
     * guest 1 (name:"Jon Snow", tableID: 1, accompanying guests: 2);
     * guest 2 (name:"Arya Stark", tableID: 2, accompanying guests: 7)
     * //
     * The guestRepository will return a list of guests.
     * Output: the guest service returns a list of Guest objects with correct information.
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
        verify(guestRepository, times(1)).getGuestList();
    }
}
