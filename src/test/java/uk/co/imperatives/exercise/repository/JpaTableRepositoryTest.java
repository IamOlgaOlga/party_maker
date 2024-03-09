package uk.co.imperatives.exercise.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.co.imperatives.exercise.configuration.TestConfig;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.data.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TestConfig.class)
public class JpaTableRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JpaTableRepository repository;

    /**
     * Input: There are 2 tables in DB
     * Output: list of tables
     */
    @Test
    public void givenTablesExistInDB_ReturnTablesList() {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("capacity", 2);
        rows.add(row1);
        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("capacity", 3);
        rows.add(row2);
        given(jdbcTemplate.queryForList(anyString())).willReturn(rows);
        List<Table> tableList =  repository.getTableList();
        assertEquals(2, tableList.size());
        assertTrue(tableList.stream().anyMatch(table -> 1 == table.getId() && 2 == table.getCapacity()));
        assertTrue(tableList.stream().anyMatch(table -> 2 == table.getId() && 3 == table.getCapacity()));
        verify(jdbcTemplate, times(1)).queryForList(anyString());
    }

    /**
     * Input: There are no tables in DB
     * Output: empty list
     */
    @Test
    public void givenTablesDoesNotExistInDB_ReturnEmptyList() {
        given(jdbcTemplate.queryForList(anyString())).willThrow(new EmptyResultDataAccessException(1));
        assertTrue(repository.getTableList().isEmpty());
    }

    /**
     * Input: table ID for the table which exists in DB
     * Table ID exists in DB.
     * Output: true
     */
    @Test
    public void givenTableIdForExistedTable_ReturnTrue() {
        int tableId = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(tableId))).willReturn(true);
        assertTrue(repository.exists(tableId));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(tableId));
    }

    /**
     * Input: table ID for the table which exists in DB
     * Table ID does not exist in DB
     * Output: false
     */
    @Test
    public void givenTableIdForNotExistedTable_ReturnFalse() {
        int tableId = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(tableId))).willReturn(null);
        assertFalse(repository.exists(tableId));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Boolean.class), eq(tableId));
    }

    /**
     * Input: table with id = 1 and capacity = 2
     * Jdbc template can update table's information in DB
     * Output: 1 updated row
     */
    @Test
    public void givenCorrectTableCapacityUpdate_ReturnOneUpdatedRow(){
        Table table = new Table(1, 2);
        given(jdbcTemplate.update(anyString(),eq(table.getCapacity()), eq(table.getId()))).willReturn(1);
        assertEquals(1, repository.updateTable(table));
        verify(jdbcTemplate, times(1)).update(anyString(),eq(table.getCapacity()), eq(table.getId()));
    }

    /**
     * Input: table with id = 1 and capacity = 2
     * Jdbc template cannot update table's information in DB
     * Output: 0 updated rows
     */
    @Test
    public void givenIncorrectTableCapacityUpdate_ReturnZeroUpdatedRows(){
        Table table = new Table(1, 2);
        given(jdbcTemplate.update(anyString(),eq(table.getCapacity()), eq(table.getId()))).willReturn(0);
        assertEquals(0, repository.updateTable(table));
        verify(jdbcTemplate, times(1)).update(anyString(),eq(table.getCapacity()), eq(table.getId()));
    }

    /**
     * Test for the getAvailableSeats() method
     * JDBC template returns available seats count from DB
     * Output: seats count.
     */
    @Test
    public void givenAvailableSeats_ReturnSeatsCount(){
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(5);
        assertEquals(5, repository.getAvailableSeats());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
    }

    /**
     * Test for the getAvailableSeats() method
     * JDBC template returns null from DB
     * Output: ExerciseServiceException must be thrown.
     */
    @Test
    public void givenJdbcTemplateReturnsNullForAvailableSeats_TrowException(){
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(null);
        Exception exception = assertThrows(ExerciseServiceException.class, () ->  repository.getAvailableSeats());
        assertEquals("Something goes wrong while calculating available seats", exception.getMessage());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class));
    }
}
