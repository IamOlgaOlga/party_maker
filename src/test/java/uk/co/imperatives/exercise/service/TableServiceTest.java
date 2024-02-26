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
     * The tableRepository will return null as table ID first time (while check the table with the same ID in DB)
     * Method saveTable() must be called.
     * The tableRepository will return table ID the second time.
     * Output: correct table ID.
     */
    @Test
    public void givenTableInfo_SaveToDbAndReturnName(){
        given(tableRepository.getTableId(1)).willReturn(null).willReturn(1);
        //
        assertThat(tableService.addTable(1, 2), equalTo(1));
        verify(tableRepository, times(2)).getTableId(anyInt());
        verify(tableRepository, times(1)).saveTable(any(Table.class));
    }

    /**
     * Input: table id = 1, capacity = 2, there is a table with id = 1 in DB.
     * //
     * The tableRepository will return the same table ID (while check the table with the same ID in DB)
     * Method saveTable() must NOT be called.
     * Output: an ExerciseServiceBadRequestException exception was thrown.
     */
    @Test
    public void givenTableInfo_NotUniq_ThrowError(){
        given(tableRepository.getTableId(1)).willReturn(1);
        //
        Exception exception = assertThrows(ExerciseServiceBadRequestException.class, () -> tableService.addTable(1, 2));
        assertEquals(exception.getMessage(), "The table with ID = 1 already exists");
        verify(tableRepository, times(1)).getTableId(anyInt());
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
        given(tableRepository.getTableId(1)).willReturn(null).willReturn(null);
        //
        Exception exception = assertThrows(ExerciseServiceException.class, () -> tableService.addTable(1, 2));
        assertEquals(exception.getMessage(), "An error occurs while saving a new guest");
        verify(tableRepository, times(2)).getTableId(anyInt());
        verify(tableRepository, times(1)).saveTable(any(Table.class));
    }
}
