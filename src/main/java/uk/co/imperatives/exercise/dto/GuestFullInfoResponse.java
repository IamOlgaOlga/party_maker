package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuestFullInfoResponse {
    private String name;
    private int table;
    @JsonProperty("accompanying_guests")
    private int accompanyingGuests;
}
