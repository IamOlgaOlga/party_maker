package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Class for guest request. Could be used for response also (for example, in case guests list)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "table", "accompanying_guests", "time_arrived"})
public class GuestRequest {

    private String name;

    @Min(value = 0, message = "Table number value must be more than 0")
    private Integer table;

    @NotNull(message = "Accompanying guests field must not be null")
    @Min(value = 0, message = "Number of accompanying guests must be more than 0")
    @JsonProperty("accompanying_guests")
    private Integer accompanyingGuests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("time_arrived")
    private Date timeArrived;

    public GuestRequest(Integer table, Integer accompanyingGuests) {
        this.table = table;
        this.accompanyingGuests = accompanyingGuests;
    }

    public GuestRequest(String name, Integer table, Integer accompanyingGuests) {
        this.name = name;
        this.table = table;
        this.accompanyingGuests = accompanyingGuests;
    }

    public GuestRequest(String name, Integer accompanyingGuests, Date timeArrived) {
        this.name = name;
        this.accompanyingGuests = accompanyingGuests;
        this.timeArrived = timeArrived;
    }
}
