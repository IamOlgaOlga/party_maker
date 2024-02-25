package uk.co.imperatives.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GuestListResponse {

    @JsonProperty("guests")
    private List<GuestFullInfoResponse> guestList;
}
