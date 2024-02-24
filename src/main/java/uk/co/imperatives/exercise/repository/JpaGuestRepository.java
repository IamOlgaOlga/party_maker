package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.repository.data.Guest;

/**
 * Repository works with an information about guests in application's DB.
 */
@AllArgsConstructor
@Repository
public class JpaGuestRepository {

    private final String SQL_INSERT_GUEST_AND_UPDATE_TABLE_RETURNING_NAME = """
            BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
            -- Insert or update tables
            INSERT INTO tables (id, capacity)
            VALUES (:targetTableId, :numberOfGuests)
            ON CONFLICT (id) DO UPDATE SET capacity = EXCLUDED.capacity + :numberOfGuests;
            -- Insert the guest into the guests table
            INSERT INTO guests (name, tableNumber, accompanyingGuests) VALUES ( :newGuestName, :targetTableId, :numberOfGuests);      
            COMMIT;
            """;

    private final String SQL_GET_GUEST_NAME = "SELECT name FROM guests WHERE name = :newGuestName;";

    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Insert a new guest to DB table and return its name.
     * @param guest information about guest (name, table number, accompanying guests)
     * @return a name of a new guest
     */
    public void updateGuestAndTable(Guest guest) {
        jdbcTemplate.update(SQL_INSERT_GUEST_AND_UPDATE_TABLE_RETURNING_NAME,
                    new MapSqlParameterSource()
                            .addValue("newGuestName", guest.getName())
                            .addValue("targetTableId", guest.getTableNumber())
                            .addValue("numberOfGuests", guest.getAccompanyingGuests()));
    }

    /**
     * Return guest's name from DB or null result if it doesn't exist.
     * @param guest information about guest.
     * @return guest's name from DB or null result if it doesn't exist.
     */
    public String getGuestName(Guest guest) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_GUEST_NAME, new MapSqlParameterSource()
                    .addValue("newGuestName", guest.getName()), String.class);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }
}
