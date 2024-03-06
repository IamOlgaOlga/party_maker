package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.data.Guest;
import uk.co.imperatives.exercise.repository.data.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for table entity in DB
 */
@Slf4j
@AllArgsConstructor
@Repository
public class JpaTableRepository {

    private final static String SQL_GET_AVAILABLE_TABLE = """
            WITH table_availability AS
            (SELECT tableNumber, SUM(accompanyingGuests) + COUNT(*) as guestsFullCount FROM guests
                WHERE tableNumber = ? GROUP BY tableNumber)
            SELECT capacity - guestsFullCount FROM tables JOIN table_availability ON tableNumber = id;
            """;

    private final static String SQL_GET_TABLE_LIST = "SELECT * FROM tables;";

    private final static String SQL_GET_TABLE_ID = "SELECT id FROM tables WHERE id = ?;";

    private final static String SQL_EXISTS_TABLE = "SELECT EXISTS (SELECT 1 FROM tables WHERE id=?);";

    private final static String SQL_SAVE_TABLE = "INSERT INTO tables (id, capacity) VALUES (?, ?);";

    private final static String SQL_UPDATE_TABLE = "UPDATE tables SET capacity=? WHERE id=?;";

    private JdbcTemplate jdbcTemplate;

    public int getTableAvailableSeats(int tableId) {
        return jdbcTemplate.queryForObject(SQL_GET_AVAILABLE_TABLE, Integer.class, tableId);
    }

    /**
     * Makes a call to DB and select all records from tables.
     * @return a list of all tables from DB
     */
    public List<Table> getTableList() {
        List<Table> tableList = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(SQL_GET_TABLE_LIST);
            for (Map<String, Object> row : rows) {
                tableList.add(new Table((Integer) row.get("id"), (Integer) row.get("capacity")));
            }
            return tableList;
        } catch (EmptyResultDataAccessException e){
            // return empty array list
            return tableList;
        }
    }

    /**
     * Saves a new table to DB
     */
    public void saveTable(Table table) {
        jdbcTemplate.update(SQL_SAVE_TABLE, table.getId(), table.getCapacity());
    }

    /**
     * Makes a call to DB and looks for the requested table ID.
     * @return table ID if it exists else null.
     */
    public Integer getTableId(int id) {
        return jdbcTemplate.queryForObject(SQL_GET_TABLE_ID, Integer.class, id);
    }

    /**
     * Check if table exists in DB.
     * @param id table ID.
     * @return true if table exists else false.
     */
    public boolean exists(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_EXISTS_TABLE, Boolean.class, id))
                .orElse(false);
    }

    /**
     * Updates table's capacity in DB by table ID.
     * @param table information about table
     */
    public void updateTable(Table table) {
        if (1 != jdbcTemplate.update(SQL_UPDATE_TABLE, table.getCapacity(), table.getId())) {
            var errorMessage = "Error in DB while table capacity update";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
    }
}
