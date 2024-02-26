package uk.co.imperatives.exercise.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
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
        if (tableRepository.getTableId(id) != null) {
            var errorMessage = "The table with ID = " + id + " already exists";
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        tableRepository.saveTable(new Table(id, capacity));
        var savedTableId = tableRepository.getTableId(id);
        if (savedTableId == null) {
            var errorMessage = "An error occurs while saving a new guest";
            log.error(errorMessage);
            throw new ExerciseServiceException(errorMessage);
        }
        return savedTableId;
    }

    public List<Table> getTablesList() {
        log.debug("Call DB to get table list");
        return tableRepository.getTableList();
    }

    public Integer updateTable(int id, int capacity) {
        if (tableRepository.getTableId(id) == null) {
            var errorMessage = "Table with ID = " + id + " does not exist";
            log.error(errorMessage);
            throw new ExerciseServiceBadRequestException(errorMessage);
        }
        tableRepository.updateTable(new Table(id, capacity));
        return tableRepository.getTableId(id);
    }
}
