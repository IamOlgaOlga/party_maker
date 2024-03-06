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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
     * Test for the addGuest() method.
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 2.
     * //
     * The guestRepository will return false while check that guest exists.
     * The tableRepository will return true while check if table exists.
     * The guestRepository will return 1 update row while calling saveGuest() method.
     * Output: correct guest name without exceptions.
     */
    @Test
    public void givenGuestInfo_AvailableSeats_SaveToDbAndReturnName(){
        String guestName = "Guest Name";
        int tableId = 1;
        int accompanyingGuests = 1;
        given(guestRepository.exists(any(Guest.class))).willReturn(false);
        given(tableRepository.exists(eq(tableId))).willReturn(true);
        given(guestRepository.saveGuest(any(Guest.class))).willReturn(1);
        //
        assertThat(guestService.addGuest(guestName, tableId, accompanyingGuests), equalTo(guestName));
        verify(guestRepository, times(1)).exists(any(Guest.class));
        verify(tableRepository, times(1)).exists(eq(tableId));
        verify(guestRepository, times(1)).saveGuest(any(Guest.class));
    }

    /**
     * Test for the addGuest() method.
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 1.
     * //
     * The guestRepository will return false while check that guest exists.
     * The tableRepository will return true while check if table exists.
     * The guestRepository will return 0 update row while calling saveGuest() method.
     * Output: an ExerciseServiceBadRequestException exception must be thrown.
     */
    @Test
    public void givenGuestInfo_NotAvailableSeats_ThrowException(){
        String guestName = "Guest Name";
        int tableId = 1;
        int accompanyingGuests = 1;
        given(guestRepository.exists(any(Guest.class))).willReturn(false);
        given(tableRepository.exists(eq(tableId))).willReturn(true);
        given(guestRepository.saveGuest(any(Guest.class))).willReturn(0);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> guestService.addGuest(guestName, tableId, accompanyingGuests));
        assertEquals(String.format("There is no free space at the table with ID = %d", tableId), exception.getMessage());
        verify(guestRepository, times(1)).exists(any(Guest.class));
        verify(tableRepository, times(1)).exists(eq(tableId));
        verify(guestRepository, times(1)).saveGuest(any(Guest.class));
    }

    /**
     * Test for the addGuest() method.
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1.
     * //
     * The guestRepository will return true while check that guest exists.
     * Output: an ExerciseServiceBadRequestException exception must be thrown.
     */
    @Test
    public void givenExistedGuestName_ThrowAnException(){
        var guestName = "Guest Name";
        int tableId = 1;
        int accompanyingGuests = 1;
        given(guestRepository.exists(any(Guest.class))).willReturn(true);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> guestService.addGuest(guestName, tableId, accompanyingGuests));
        assertEquals(String.format("Guest with name %s already exists", guestName), exception.getMessage());
        verify(guestRepository, times(1)).exists(any(Guest.class));
        verify(tableRepository, times(0)).exists(anyInt());
        verify(guestRepository, times(0)).saveGuest(any(Guest.class));
    }

    /**
     * Test for the addGuest() method.
     * Input: guest Name = "Guest Name", tableId = 1, accompanying guests = 1, available seats on table 1 is 2.
     * //
     * The guestRepository will return false while check that guest exists.
     * The tableRepository will return false while check if table exists.
     * Output: an ExerciseServiceException exception must be thrown.
     */
    @Test
    public void givenNotExistedTableWhileSavingGuest_ThrowAnException(){
        var guestName = "Guest Name";
        int tableId = 1;
        int accompanyingGuests = 1;
        given(guestRepository.exists(any(Guest.class))).willReturn(false);
        given(tableRepository.exists(eq(tableId))).willReturn(false);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> guestService.addGuest(guestName, tableId, accompanyingGuests));
        assertEquals(String.format("There is no table with ID = %d", tableId), exception.getMessage());
        verify(guestRepository, times(1)).exists(any(Guest.class));
        verify(tableRepository, times(1)).exists(anyInt());
        verify(guestRepository, times(0)).saveGuest(any(Guest.class));
    }

    /**
     * Test for the getGuestList() method.
     * Input:
     * guest 1 (name:"Jon Snow", tableID: 1, accompanying guests: 2);
     * guest 2 (name:"Arya Stark", tableID: 2, accompanying guests: 7)
     * //
     * The guestRepository will return a list of guests.
     * Output: the guest service returns a list of Guest objects with correct information.
     */
    @Test
    public void givenRequestForGuestList_ReturnTheCorrectListOfGuests() {
        Guest guest1 = new Guest("Jon Snow", 1, 2);
        Guest guest2 = new Guest("Arya Stark", 2, 7);
        List<Guest> guestList = new ArrayList<>(2);
        guestList.add(guest1);
        guestList.add(guest2);
        given(guestRepository.getGuestList()).willReturn(guestList);
        List<Guest> resultList = guestService.getGuestList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.stream().anyMatch(guest -> "Jon Snow".equals(guest.getName())
                        && 1 == guest.getTableNumber() && 2 == guest.getTotalGuests()));
        assertTrue(resultList.stream().anyMatch(guest -> "Arya Stark".equals(guest.getName())
                && 2 == guest.getTableNumber() && 7 == guest.getTotalGuests()));
        verify(guestRepository, times(1)).getGuestList();
    }

    /**
     * Test for method checkInGuest()
     * Input: guest name "Jon Snow" and accompanying guest = 2.
     * //
     * Guest repository returns guest name while checks that the guest booked a table.
     * Guest repository returns 1 updated row while checks availability of the table's space and
     * change information about arrived guest.
     * Guest repository returns guest name the second time if everything is OK.
     * Output: service returns guest's name.
     */
    @Test
    public void givenArrivedGuest_AvailableTableSpace_ReturnGuestName() {
        String guestName = "Guest Name";
        int accompanyingGuests = 1;
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(guestName).willReturn(guestName);
        given(guestRepository.updateArrivedGuest(any(Guest.class))).willReturn(1);
        assertThat(guestService.checkInGuest(guestName, accompanyingGuests), equalTo(guestName));
        verify(guestRepository, times(2)).getGuestName(any(Guest.class));
        verify(guestRepository, times(1)).updateArrivedGuest(any(Guest.class));
    }

    /**
     * Test for method checkInGuest()
     * Input: guest name "Jon Snow" and accompanying guest = 2.
     * //
     * Guest repository doesn't return guest name while checks that the guest booked a table.
     * Service throws a correct exception for this case.
     * Output: an ExerciseServiceBadRequestException exception must be thrown.
     */
    @Test
    public void givenArrivedGuest_GuestDidNotBookTable_ThrowException() {
        String guestName = "Guest Name";
        int accompanyingGuests = 1;
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(null);
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> guestService.checkInGuest(guestName, accompanyingGuests));
        assertEquals(String.format("Guest with name %s did not book a table", guestName), exception.getMessage());
        verify(guestRepository, times(1)).getGuestName(any(Guest.class));
        verify(guestRepository, times(0)).updateArrivedGuest(any(Guest.class));
    }

    /**
     * Test for method checkInGuest()
     * Input: guest name "Jon Snow" and accompanying guest = 2.
     * //
     * Guest repository returns guest name while checks that the guest booked a table.
     * Guest repository returns 0 updated row while checks availability of the table's space and
     * change information about arrived guest.
     * Service throws a correct exception for this case.
     * Output: an ExerciseServiceBadRequestException exception must be thrown.
     */
    @Test
    public void givenArrivedGuest_NotAvailableTableSpace_ThrowException() {
        String guestName = "Guest Name";
        int accompanyingGuests = 1;
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(guestName);
        given(guestRepository.updateArrivedGuest(any(Guest.class))).willReturn(0);
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> guestService.checkInGuest(guestName, accompanyingGuests));
        assertEquals(
                String.format("Booked table does not have available space for %d guests (main guest name is %s)",
                        accompanyingGuests, guestName),
                exception.getMessage());
        verify(guestRepository, times(2)).getGuestName(any(Guest.class));
        verify(guestRepository, times(1)).updateArrivedGuest(any(Guest.class));
    }

    /**
     * Test for method checkInGuest()
     * Input: guest name "Jon Snow" and accompanying guest = 2.
     * //
     * Guest repository returns guest name while checks that the guest booked a table.
     * Guest repository returns 1 updated row while checks availability of the table's space and
     * change information about arrived guest.
     * Guest repository doesn't return guest name the second time.
     * Output: an ExerciseServiceException exception must be thrown.
     */
    @Test
    public void givenArrivedGuest_SomeServiceException_ThrowException() {
        String guestName = "Guest Name";
        int accompanyingGuests = 1;
        given(guestRepository.getGuestName(any(Guest.class))).willReturn(guestName).willReturn(null);
        given(guestRepository.updateArrivedGuest(any(Guest.class))).willReturn(1);
        Exception exception = assertThrows(ExerciseServiceException.class,
                () -> guestService.checkInGuest(guestName, accompanyingGuests));
        assertEquals("An error was occur while updating an information about arrived guest", exception.getMessage());
        verify(guestRepository, times(2)).getGuestName(any(Guest.class));
        verify(guestRepository, times(1)).updateArrivedGuest(any(Guest.class));
    }
}
