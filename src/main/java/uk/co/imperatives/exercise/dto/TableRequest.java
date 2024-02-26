package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The class for requests with information. Could be used for response also (for example, in case tables list)
 */
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"table_id", "capacity"})
public class TableRequest {

    @JsonProperty("table_id")
    private Integer tableId;

    @Min(value = 0, message = "Capacity value must be positive or equals 0")
    @NotNull
    private int capacity;
}
