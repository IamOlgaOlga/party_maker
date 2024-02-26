package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TableListResponse {

    @JsonProperty("tables_list")
    private List<TableRequest> tablesList;
}
