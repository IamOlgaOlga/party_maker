package uk.co.imperatives.exercise.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class JpaGuestRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JpaGuestRepository repository;

    /**
     * Test for the getGuestName() method.
     * Input: guest with name "Jon Snow".
     * Repository can find this guest and return the name.
     * Output: correct guest name.
     */
    @Test
    public void givenGuestExists_ReturnName() {
        String guestName = "Jon Snow";
        given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(guestName))).willReturn(guestName);
        assertEquals(repository.getGuestName(new Guest(guestName, 1, 1)), guestName);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(String.class), eq(guestName));
    }

    /**
     * Test for the getGuestName() method.
     * Input: guest with name "Jon Snow".
     * Repository can't find this guest in DB and throw EmptyResultDataAccessException exception.
     * Output: null.
     */
    @Test
    public void givenGuestDoesNotExist_ReturnNull() {
        String guestName = "Jon Snow";
        given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(guestName))).willThrow(new EmptyResultDataAccessException(1));
        assertNull(repository.getGuestName(new Guest(guestName, 1, 1)));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(String.class), eq(guestName));
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
     * Test for the updateArrivedGuest() method.
     */
    @Test
    public void givenAvailableSpaceAtTableInDB_Return1() {
        String guestName = "Jon Snow";
        int accompanyingGuests = 2;
        given(jdbcTemplate.update(anyString(),eq(guestName), eq(accompanyingGuests))).willReturn(1);
        assertTrue(repository.getGuestList().isEmpty());
        verify(jdbcTemplate, times(1)).update(anyString(),eq(guestName), eq(accompanyingGuests));
    }
}
