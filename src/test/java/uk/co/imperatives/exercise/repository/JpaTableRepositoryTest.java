package uk.co.imperatives.exercise.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.data.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
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
     * Output: table ID
     */
    @Test
    public void givenTableIdForExistedTable_ReturnTableId() {
        int tableId = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(tableId))).willReturn(tableId);
        assertEquals(tableId, repository.getTableId(tableId));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class), eq(tableId));
    }

    /**
     * Input: table ID for the table which exists in DB
     * Output: table ID
     */
    @Test
    public void givenTableIdForNotExistedTable_ReturnNull() {
        int tableId = 1;
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(tableId))).willThrow(new EmptyResultDataAccessException(1));
        assertNull(repository.getTableId(tableId));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class), eq(tableId));
    }

    /**
     * Jdbc template can update table's information in DB
     */
    @Test
    public void givenCorrectTableCapacityUpdate_NothingIsThrown(){
        Table table = new Table(1, 2);
        given(jdbcTemplate.update(anyString(),eq(table.getCapacity()), eq(table.getId()))).willReturn(1);
        repository.updateTable(table);
        verify(jdbcTemplate, times(1)).update(anyString(),eq(table.getCapacity()), eq(table.getId()));
    }

    /**
     * Jdbc template cannot update table's information in DB
     * Exception will be thrown
     */
    @Test
    public void givenIncorrectTableCapacityUpdate_ExceptionIsThrown(){
        Table table = new Table(1, 2);
        given(jdbcTemplate.update(anyString(),eq(table.getCapacity()), eq(table.getId()))).willReturn(0);
        Exception exception = assertThrows(ExerciseServiceException.class, () -> repository.updateTable(table));
        assertEquals(exception.getMessage(), "Error in DB while table capacity update");
        verify(jdbcTemplate, times(1)).update(anyString(),eq(table.getCapacity()), eq(table.getId()));
    }
}
