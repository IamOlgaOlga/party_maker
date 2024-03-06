package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class for guest request. Could be used for response also (for example, in case guests list)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequest {

    private String name;

    @Min(value = 0, message = "Table number value must be more than 0")
    private Integer table;

    @NotNull(message = "Accompanying guests field must not be null")
    @Min(value = 0, message = "Accompanying Guests must be more than 0")
    @JsonProperty("accompanying_guests")
    private Integer accompanyingGuests;
}
