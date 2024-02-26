package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TableResponse {

    @JsonProperty("table_id")
    private int tableId;
}
