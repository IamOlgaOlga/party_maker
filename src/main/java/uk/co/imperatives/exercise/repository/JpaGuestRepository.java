package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.repository.data.Guest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository works with an information about guests in application's DB.
 */
@AllArgsConstructor
@Repository
public class JpaGuestRepository {

    private final String SQL_INSERT_GUEST = """
                    WITH taken_places AS
                    	(SELECT SUM(total_guests) AS people, table_number 
                    	    FROM guests 
                    	    GROUP BY table_number)
                    INSERT INTO guests (name, table_number, total_guests)
                    SELECT ?, ?, ?
                    WHERE ? <= (SELECT (capacity - COALESCE(people, 0)) 
                                    FROM tables JOIN taken_places 
                                    ON table_number = id 
                                    WHERE id = ?);
            """;

    private final String SQL_GET_GUEST_NAME = "SELECT name FROM guests WHERE name = ?;";

    private final String SQL_EXISTS_GUEST_NAME = "SELECT EXISTS (SELECT 1 FROM guests WHERE name=?);";

    private final String SQL_SELECT_ALL_FROM_GUESTS = "SELECT * FROM guests;";

    private JdbcTemplate jdbcTemplate;

    /**
     * Insert a new guest to DB table and return its name.
     * @param guest information about guest (name, table number, accompanying guests)
     * @return number of inserted rows
     */
    public int saveGuest(Guest guest) {
        return jdbcTemplate.update(SQL_INSERT_GUEST,
                guest.getName(), guest.getTableNumber(), guest.getTotalGuests(), guest.getTotalGuests(), guest.getTableNumber());
    }

    /**
     * Check if guest exists in DB.
     * @param guest information about guest.
     * @return true if guest exists else false.
     */
    public boolean exists(Guest guest) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_EXISTS_GUEST_NAME, Boolean.class, guest.getName()))
                .orElse(false);
    }

    /**
     * Return guest's name from DB or null result if it doesn't exist.
     * @param guest information about guest.
     * @return guest's name from DB or null result if it doesn't exist.
     */
    public String getGuestName(Guest guest) {
        return jdbcTemplate.queryForObject(SQL_GET_GUEST_NAME, String.class, guest.getName());
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

    /**
     * This method checks available space at the booked table for arrived guest and accompanying friends
     * and update an information about arrived guest.
     * @param guest an arrived guest
     * @return update rows. 1 in case success update else 0.
     */
    public int updateArrivedGuest(Guest guest) {
        return 1;
    }
}
