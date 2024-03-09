package uk.co.imperatives.exercise.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.entity.Table;

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

    private final static String SQL_GET_TABLE_LIST = "SELECT * FROM tables;";

    private final static String SQL_EXISTS_TABLE = "SELECT EXISTS (SELECT 1 FROM tables WHERE id=?);";

    private final static String SQL_INSERT_TABLE = "INSERT INTO tables (id, capacity) VALUES (?, ?);";

    private final static String SQL_UPDATE_TABLE = "UPDATE tables SET capacity=? WHERE id=?;";

    private final static String SQL_SELECT_AVAILABLE_SEATS = """
            WITH
                t AS (SELECT SUM(capacity) capacity FROM tables),
                a AS (SELECT COALESCE(SUM(count), 0) taken_seats FROM arrived_guests)
            SELECT t.capacity - a.taken_seats FROM a, t;
            """;

    private JdbcTemplate jdbcTemplate;

    /**
     * Makes a call to DB and select all records from tables.
     *
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
        } catch (EmptyResultDataAccessException e) {
            // return empty array list
            return tableList;
        }
    }

    /**
     * Saves a new table to DB
     *
     * @return number of inserted rows
     */
    public int saveTable(Table table) {
        return jdbcTemplate.update(SQL_INSERT_TABLE, table.getId(), table.getCapacity());
    }

    /**
     * Check if table exists in DB.
     *
     * @param id table ID.
     * @return true if table exists else false.
     */
    public boolean exists(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_EXISTS_TABLE, Boolean.class, id))
                .orElse(false);
    }

    /**
     * Updates table's capacity in DB by table ID.
     *
     * @param table information about table
     * @return number of updated rows
     */
    public int updateTable(Table table) {
        return jdbcTemplate.update(SQL_UPDATE_TABLE, table.getCapacity(), table.getId());
    }

    /**
     * Get a count of available seats from DB
     *
     * @return count of available seats
     */
    public int getAvailableSeats() {
        return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_SELECT_AVAILABLE_SEATS, Integer.class))
                .orElseThrow(() -> new ExerciseServiceException("Something goes wrong while calculating available seats"));
    }
}
