package uk.co.imperatives.exercise.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.JpaTableRepository;
import uk.co.imperatives.exercise.repository.data.Table;

import java.util.List;

/**
 * Service to manage tables.
 */
@Slf4j
@AllArgsConstructor
@Service
public class TableService {

    private JpaTableRepository tableRepository;

    /**
     * This method аdd a new table to the table list.
     * If the table with the same ID already exists, an exception will be thrown.
     * @param id table ID
     * @param capacity table capacity
     * @return added table ID
     */
    public int addTable(int id, int capacity) {
        if (tableRepository.exists(id)) {
            var errorMessage = String.format("Table with ID = %d already exists", id);
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        var insertedRows = tableRepository.saveTable(new Table(id, capacity));
        if (insertedRows == 0) {
            var errorMessage = "An error occurs while saving a new table";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return id;
    }

    public List<Table> getTablesList() {
        log.debug("Call DB to get table list");
        return tableRepository.getTableList();
    }

    /**
     * Update table's capacity by ID.
     * @param id table's ID
     * @param capacity new table's capacity
     * @return updated table ID
     */
    public Integer updateTable(int id, int capacity) {
        if (!tableRepository.exists(id)) {
            var errorMessage = String.format("Table with ID = %d does not exist", id);
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        var updatedRows = tableRepository.updateTable(new Table(id, capacity));
        if (1 != updatedRows) {
            var errorMessage = "Error in DB while table capacity update";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return id;
    }
}
