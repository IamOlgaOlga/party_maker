package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaTableRepository;
import uk.co.imperatives.exercise.repository.data.Table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for TableService
 */
@SpringBootTest
public class TableServiceTest {

    @Mock
    private JpaTableRepository tableRepository;

    @InjectMocks
    private TableService tableService;

    /**
     * Input: table id = 1, capacity = 2, there is no table with id = 1 in DB.
     * //
     * The tableRepository will return false while checks the table exists in DB
     * The tableRepository will return 1 as inserted rows while calling saveTable() method.
     * Output: correct table ID.
     */
    @Test
    public void givenTableInfo_SaveToDbAndReturnName(){
        var tableId = 1;
        var capacity = 2;
        given(tableRepository.exists(eq(tableId))).willReturn(false);
        given(tableRepository.saveTable(any(Table.class))).willReturn(1);
        //
        assertThat(tableService.addTable(tableId, capacity), equalTo(1));
        verify(tableRepository, times(1)).exists(anyInt());
        verify(tableRepository, times(1)).saveTable(any(Table.class));
    }

    /**
     * Input: table id = 1, capacity = 2, there is a table with id = 1 in DB.
     * //
     * The tableRepository will return true while checks the table exists in DB
     * Method saveTable() of tableRepository must NOT be called.
     * Output: an ExerciseServiceBadRequestException exception was thrown.
     */
    @Test
    public void givenTableInfo_NotUniq_ThrowError(){
        var tableId = 1;
        var capacity = 2;
        given(tableRepository.exists(eq(tableId))).willReturn(true);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class,
                () -> tableService.addTable(tableId, capacity));
        assertEquals(String.format("Table with ID = %d already exists", tableId), exception.getMessage());
        verify(tableRepository, times(1)).exists(anyInt());
        verify(tableRepository, times(0)).saveTable(any(Table.class));
    }

    /**
     * Input: table id = 1, capacity = 2, there is no table with id = 1 in DB.
     * //
     * The tableRepository will return null as table ID first time (while check the table with the same ID in DB)
     * Method saveTable() must be called.
     * The tableRepository will return table ID the second time.
     * Output: an ExerciseServiceException exception was thrown.
     */
    @Test
    public void givenTableInfo_NotSaved_ThrowError(){
        var tableId = 1;
        var capacity = 2;
        given(tableRepository.exists(eq(tableId))).willReturn(false);
        given(tableRepository.saveTable(any(Table.class))).willReturn(0);
        //
        Exception exception = assertThrows(ExerciseServiceException.class, () -> tableService.addTable(tableId, capacity));
        assertEquals("An error occurs while saving a new table", exception.getMessage());
        verify(tableRepository, times(1)).exists(anyInt());
        verify(tableRepository, times(1)).saveTable(any(Table.class));
    }
}
