package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
                    	(SELECT SUM(total_guests)::int AS people, table_number 
                    	    FROM guests 
                    	    GROUP BY table_number)
                    INSERT INTO guests (name, table_number, total_guests)
                    SELECT :name, :tableId, :guests
                    WHERE :guests <= (SELECT (capacity - COALESCE(people, 0)) 
                                    FROM tables LEFT JOIN taken_places 
                                    ON table_number = id 
                                    WHERE id = :tableId);
            """;

    private final String SQL_EXISTS_GUEST_NAME = "SELECT EXISTS (SELECT 1 FROM guests WHERE name=?);";

    private final String SQL_SELECT_ALL_FROM_GUESTS = "SELECT * FROM guests;";

    private final String SQL_INSERT_ARRIVAL_GUEST = """
            WITH 
                taken_places AS
                    (SELECT SUM(count)::int AS people, table_number
                    	    FROM arrived_guests a RIGHT JOIN guests g ON a.name = g.name
                    	    GROUP BY table_number),
                guest_table_info AS
                    (SELECT name, table_number, capacity
                    	    FROM guests JOIN tables ON table_number=id)
            INSERT INTO arrived_guests (name, count)
                SELECT :name, :guests
                WHERE :guests <= (SELECT (capacity - COALESCE(people, 0)) 
                                    FROM guest_table_info g LEFT JOIN taken_places t
                                    ON g.table_number = t.table_number
                                    WHERE name = :name);
            """;

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Insert a new guest to DB table and return its name.
     * @param guest information about guest (name, table number, accompanying guests)
     * @return number of inserted rows
     */
    public int saveGuest(Guest guest) {
        return namedParameterJdbcTemplate.update(SQL_INSERT_GUEST,
                new MapSqlParameterSource()
                        .addValue("name", guest.getName())
                        .addValue("tableId", guest.getTableNumber())
                        .addValue("guests", guest.getTotalGuests())
        );
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
        return namedParameterJdbcTemplate.update(SQL_INSERT_ARRIVAL_GUEST,
                new MapSqlParameterSource()
                        .addValue("name", guest.getName())
                        .addValue("guests", guest.getTotalGuests())
        );
    }
}
