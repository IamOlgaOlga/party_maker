package uk.co.imperatives.exercise.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class JpaGuestRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private JpaGuestRepository repository;

    /**
     * Test for the exists() method.
     * Input: guest with name "Jon Snow".
     * Repository can find this guest and return true.
     * Output: true.
     */
    @Test
    public void givenGuestExists_ReturnTrue() {
        var guestName = "Jon Snow";
        var tableId = 1;
        var totalGuests = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(guestName))).willReturn(true);
        assertTrue(repository.exists(new Guest(guestName, tableId, totalGuests)));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(guestName));
    }

    /**
     * Test for the exists() method.
     * Input: guest with name "Jon Snow".
     * Repository can't find this guest in DB and return false.
     * Output: false.
     */
    @Test
    public void givenGuestDoesNotExist_ReturnFalse() {
        var guestName = "Jon Snow";
        var tableId = 1;
        var totalGuests = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(guestName))).willReturn(null);
        assertFalse(repository.exists(new Guest(guestName, tableId, totalGuests)));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(guestName));
    }

    /**
     * Test for the arrived() method.
     * Input: guest with name "Jon Snow".
     * Repository can find this guest an arrived list and return true.
     * Output: true.
     */
    @Test
    public void givenGuestArrived_ReturnTrue() {
        var guestName = "Jon Snow";
        var tableId = 1;
        var totalGuests = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(guestName))).willReturn(true);
        assertTrue(repository.arrived(new Guest(guestName, tableId, totalGuests)));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(guestName));
    }

    /**
     * Test for the arrived() method.
     * Input: guest with name "Jon Snow".
     * Repository can't find this guest in arrived list and return false.
     * Output: false.
     */
    @Test
    public void givenGuestDidNotArrive_ReturnFalse() {
        var guestName = "Jon Snow";
        var tableId = 1;
        var totalGuests = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(guestName))).willReturn(null);
        assertFalse(repository.arrived(new Guest(guestName, tableId, totalGuests)));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(guestName));
    }

    /**
     * Test for the saveGuest() method
     * Input: guest Jon Snow, table ID = 1, total guest = 2.
     * JDBC template returns 1 updated row.
     * Output: returns 1.
     */
    @Test
    public void givenGuestInfo_AvailableTableSeats_ReturnOneUpdatedRow() {
        var guest = new Guest("Jon Snow", 1, 2);
        given(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class))).willReturn(1);
        assertEquals(1, repository.saveGuest(guest));
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    /**
     * Test for the saveGuest() method
     * Input: guest Jon Snow, table ID = 1, total guest = 2.
     * JDBC template returns 0 updated row.
     * Output: returns 0.
     */
    @Test
    public void givenGuestInfo_AvailableTableSeats_ReturnZeroUpdatedRow() {
        var guest = new Guest("Jon Snow", 1, 2);
        given(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class))).willReturn(0);
        assertEquals(0, repository.saveGuest(guest));
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    /**
     * Test for the getGuestList() method.
     * Input: Repository can find 2 guests in DB.
     * Output: a list with 2 guests.
     */
    @Test
    public void givenNotEmptyGuestsListInDB_ReturnListOfGuests() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("name", "Jon Snow");
        row1.put("tableNumber", 1);
        row1.put("accompanyingGuests", 2);
        rows.add(row1);
        Map<String, Object> row2 = new HashMap<>();
        row2.put("name", "Arya Stark");
        row2.put("tableNumber", 2);
        row2.put("accompanyingGuests", 3);
        rows.add(row2);
        given(jdbcTemplate.queryForList(anyString())).willReturn(rows);
        List<Guest> guestList =  repository.getGuestList();
        assertEquals(2, guestList.size());
        assertTrue(guestList.stream().anyMatch(guest -> "Jon Snow".equals(guest.getName())
                && 1 == guest.getTableNumber() && 2 == guest.getTotalGuests()));
        assertTrue(guestList.stream().anyMatch(guest -> "Arya Stark".equals(guest.getName())
                && 2 == guest.getTableNumber() && 3 == guest.getTotalGuests()));
        verify(jdbcTemplate, times(1)).queryForList(anyString());
    }

    /**
     * Test for the getGuestList() method.
     * Input: Repository can't find anything in DB.
     * Output: an empty list.
     */
    @Test
    public void givenEmptyGuestsListInDB_ReturnEmptyList() {
        given(jdbcTemplate.queryForList(anyString())).willThrow(new EmptyResultDataAccessException(1));
        assertTrue(repository.getGuestList().isEmpty());
        verify(jdbcTemplate, times(1)).queryForList(anyString());
    }

    /**
     * Test for updateArrivedGuest() method.
     * Input: guest Jon Snow, table ID = null, total guest = 2.
     * JDBC template returns 1 updated row.
     * Output: returns 1.
     */
    @Test
    public void givenGuestArrived_SuccessUpdateInfo_ReturnOneUpdatedRow() {
        var guest = new Guest("Jon Snow", null, 2);
        given(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class))).willReturn(1);
        assertEquals(1, repository.updateArrivedGuest(guest));
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    /**
     * Test for updateArrivedGuest() method.
     * Input: guest Jon Snow, table ID = null, total guest = 2.
     * JDBC template returns 0 updated row.
     * Output: returns 0.
     */
    @Test
    public void givenGuestArrived_SuccessUpdateInfo_ReturnZeroUpdatedRow() {
        var guest = new Guest("Jon Snow", null, 2);
        given(namedParameterJdbcTemplate.update(anyString(), any(SqlParameterSource.class))).willReturn(0);
        assertEquals(0, repository.updateArrivedGuest(guest));
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(), any(SqlParameterSource.class));
    }

    /**
     * Test for deleteGuest() method.
     * Input: guest Jon Snow, table ID = null, total guest = null.
     * JDBC template returns 1 removed row.
     * Output: returns 1.
     */
    @Test
    public void givenGuest_RemovedFromDB_ReturnOneAffectedRow() {
        var guest = new Guest("Jon Snow", null, null);
        given(jdbcTemplate.update(anyString(),anyString())).willReturn(1);
        assertEquals(1, repository.deleteGuest(guest));
        verify(jdbcTemplate, times(1)).update(anyString(), anyString());
    }


    /**
     * Test for deleteGuest() method.
     * Input: guest Jon Snow, table ID = null, total guest = null.
     * JDBC template returns 0 removed row.
     * Output: returns 0.
     */
    @Test
    public void givenGuest_NotRemovedFromDB_ReturnZeroAffectedRow() {
        var guest = new Guest("Jon Snow", null, null);
        given(jdbcTemplate.update(anyString(),anyString())).willReturn(0);
        assertEquals(0, repository.deleteGuest(guest));
        verify(jdbcTemplate, times(1)).update(anyString(), anyString());
    }
}
