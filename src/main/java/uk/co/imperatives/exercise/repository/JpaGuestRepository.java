package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository works with an information about guests in application's DB.
 */
@AllArgsConstructor
@Repository
public class JpaGuestRepository {

    private final String SQL_INSERT_GUEST = "INSERT INTO guests (name, tableNumber, accompanyingGuests) VALUES ( ?, ?, ?);";

    private final String SQL_GET_GUEST_NAME = "SELECT name FROM guests WHERE name = :newGuestName;";

    private final String SQL_SELECT_ALL_FROM_GUESTS = "SELECT * FROM GUESTS;";

    private JdbcTemplate jdbcTemplate;

    /**
     * Insert a new guest to DB table and return its name.
     * @param guest information about guest (name, table number, accompanying guests)
     */
    public void saveGuest(Guest guest) {
        jdbcTemplate.update(SQL_INSERT_GUEST, guest.getName(), guest.getTableNumber(), guest.getAccompanyingGuests());
    }

    /**
     * Return guest's name from DB or null result if it doesn't exist.
     * @param guest information about guest.
     * @return guest's name from DB or null result if it doesn't exist.
     */
    public String getGuestName(Guest guest) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_GUEST_NAME, String.class, guest.getName());
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    /**
     * This method go to the DB and return guests list from DB.
     * @return guests list from DB. If there is nothing in DB, this method returns an empty array list.
     */
    public List<Guest> getGuestList() {
        List<Guest> guestList = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(SQL_SELECT_ALL_FROM_GUESTS);
            for (Map<String, Object> row : rows) {
                Guest guest = new Guest((String) row.get("name"), (Integer) row.get("tableNumber"),
                        (Integer) row.get("accompanyingGuests"));
                guestList.add(guest);
            }
            return guestList;
        } catch (EmptyResultDataAccessException e){
            // return empty array list
            return guestList;
        }
    }
}
