package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.repository.data.Guest;

/**
 * Repository works with an information about guests in application's DB.
 */
@AllArgsConstructor
@Repository
public class JpaGuestRepository {

    private final String SQL_INSERT_GUEST_RETURNING_NAME = "insert into guests(name, tableNumber, accompanyingGuests) values(?,?,?) returning name";

    private JdbcTemplate jdbcTemplate;

    /**
     * Insert a new guest to DB table and return its name.
     * @param guest information about guest (name, table number, accompanying guests)
     * @return a name of a new guest
     */
    public String save(Guest guest) {
        return String.valueOf(jdbcTemplate.update(SQL_INSERT_GUEST_RETURNING_NAME,
                guest.getName(), guest.getTableNumber(), guest.getAccompanyingGuests()));
    }
}
