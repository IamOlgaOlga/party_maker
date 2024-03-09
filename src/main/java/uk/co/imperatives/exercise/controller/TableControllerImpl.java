package uk.co.imperatives.exercise.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.imperatives.exercise.dto.TableListResponse;
import uk.co.imperatives.exercise.dto.TableRequest;
import uk.co.imperatives.exercise.dto.TableResponse;
import uk.co.imperatives.exercise.service.TableService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class TableControllerImpl implements TableController {

    private TableService tableService;

    /**
     * This method аdd a new table to the table list.
     * If the table with the same ID already exists, an exception will be thrown.
     *
     * @param tableRequest information about table: table's number and count of accompanying guests
     * @return created table's ID
     */
    @PostMapping("/table")
    public ResponseEntity<TableResponse> addTable(@RequestBody @Valid TableRequest tableRequest) {
        log.debug("Receive a new POST request to add a new table (table ID = " + tableRequest.getTableId()
                + ", capacity = " + tableRequest.getCapacity() + ")");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TableResponse(tableService.addTable(tableRequest.getTableId(), tableRequest.getCapacity())));
    }

    /**
     * This method provides the tables list.
     *
     * @return tables list
     */
    @GetMapping("/tables_list")
    public @ResponseBody TableListResponse getTablesList() {
        log.debug("Receive a new GET request to provide a table list");
        List<TableRequest> tablesList = new ArrayList<>();
        tableService.getTablesList()
                .forEach(table -> tablesList.add(new TableRequest(table.getId(), table.getCapacity())));
        return new TableListResponse(tablesList);
    }

    /**
     * This method provides the tables list.
     *
     * @return tables list
     */
    @PutMapping("/table/{id}")
    public @ResponseBody TableResponse updateTable(@PathVariable(name = "id") Integer id, @RequestBody @Valid TableRequest tableRequest) {
        log.debug("Receive a new PUT request to update table capacity");
        return new TableResponse(tableService.updateTable(id, tableRequest.getCapacity()));
    }
}
