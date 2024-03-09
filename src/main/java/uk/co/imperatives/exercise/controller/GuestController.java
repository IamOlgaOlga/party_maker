package uk.co.imperatives.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.co.imperatives.exercise.dto.GuestListResponse;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.dto.GuestResponse;
import uk.co.imperatives.exercise.dto.SeatsResponse;


@Tag(name = "Guests Controller", description = "Controller to manage guests")
public interface GuestController {

    @Operation(summary = "Add a new guest",
            description = "Add a new guest to the guest list. Throws an error if there is insufficient table space.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created a new guest and add it to the guests list",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 201 will be returned if a new guest was created and added to the guests list",
                                    summary = "New guest was created",
                                    value = "{\"name\": \"Jon Snow\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 400 will be returned in case invalid value of request parameters",
                                    summary = "Invalid value of accompanying guests",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 400, " +
                                            "\"error_message\":\"Number of accompanying guests must be more than 0\"}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "409", description = "Guest already exists",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 409 will be returned if the requested guest already exists",
                                    summary = "Guest already exists",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 409, " +
                                            "\"error_message\":\"Guest with name Jon Snow already exists\"}"
                            )
                    )}
            )
    })
    ResponseEntity<GuestResponse> addGuest(
            @Parameter(description = "Name of the guest", required = true, example = "Jon Snow")
            String name,
            @RequestBody(description = "Guest information", required = true, content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestRequest.class),
                    examples = @ExampleObject(
                            name = "Add a new guest with 3 friends to the table 1",
                            summary = "1+3 guests for table 1",
                            value = "{\"table\": 1, \"accompanying_guests\": 3}"
                    ))
            )
            GuestRequest guestRequest);

    @Operation(summary = "Get the guest list who booked the table", description = "Provides a list with information about all guests who booked a table")
    @ApiResponse(
            responseCode = "200",
            description = "A guests list",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestListResponse.class),
                    examples = @ExampleObject(
                            name = "Status 200 and a list with information about guests will be returned",
                            summary = "guests list",
                            value = " {\"guests\":["
                                    + "{\"name\":\"Jon Snow\", \"table\":1, \"accompanying_guests\":1}, "
                                    + "{\"name\":\"Arya Stark\", \"table\":2,\"accompanying_guests\":1}, "
                                    + "{\"name\":\"Tyrion Lannister\", \"table\":3, \"accompanying_guests\":2}]}"
                    )
            )}
    )
    GuestListResponse getGuestList();

    @Operation(summary = "Check in an arrived guest", description = "Manages an arrived guest. Allows extra friends if space is available.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Guest checked in",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 200 will be returned if requested guest was checked in",
                                    summary = "Guest checked in",
                                    value = "{\"name\": \"Jon Snow\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 400 will be returned in case invalid value of request parameters",
                                    summary = "Invalid value of accompanying guests",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 400, " +
                                            "\"error_message\":\"Number of accompanying guests must be more than 0\"}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "404", description = "Guest does not exist",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 404 will be returned if the requested guest does not exist (didn't booked any table)",
                                    summary = "Guest does not exist",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 404, " +
                                            "\"error_message\":\"Guest with name Jon Snow did not book a table\"}"
                            )
                    )}
            )
    })
    GuestResponse arrivedGuest(
            @Parameter(description = "Name of the guest", required = true, example = "Jon Snow")
            String name,
            @RequestBody(description = "Guest information", required = true, content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestRequest.class),
                    examples = @ExampleObject(
                            name = "A new guest with 3 friends arrived",
                            summary = "1+3 guests arrived",
                            value = "{\"accompanying_guests\": 3}"
                    ))
            )
            GuestRequest guestRequest);

    @Operation(summary = "Delete an arrived guest", description = "In case guest leaves the party, delete the guest. " +
            "When a guest leaves, all their accompanying friends leave as well.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Guest was deleted",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 200 will be returned if requested guest was deleted",
                                    summary = "Guest was deleted",
                                    value = "{\"name\": \"Jon Snow\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Guest does not exist",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 404 will be returned if the requested guest does not exist (didn't booked any table)",
                                    summary = "Guest does not exist",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 404, " +
                                            "\"error_message\":\"Guest with name Jon Snow did not book a table\"}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurs",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 500 will be returned if unexpected error occurs on the service side",
                                    summary = "Unexpected error",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 500, " +
                                            "\"error_message\":\"Some errors occurs while removing the guest with name = Jon Snow\"}"
                            )
                    )}
            )
    })
    GuestResponse deleteGuest(@Parameter(description = "Name of the guest", required = true, example = "Jon Snow") String name);

    @Operation(summary = "Get the arrived guest list", description = "Provides a list with information about all guests who have arrived to the party")
    @ApiResponse(
            responseCode = "200",
            description = "An arrived guests list",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestListResponse.class),
                    examples = @ExampleObject(
                            name = "Status 200 and a list with information about arrived guests will be returned",
                            summary = "arrived guests list",
                            value = " {\"guests\":["
                                    + "{\"name\":\"Jon Snow\", \"accompanying_guests\":1, \"time_arrived\":\"2024-01-01 05:20:22\"}, "
                                    + "{\"name\":\"Arya Stark\",\"accompanying_guests\":1,\"time_arrived\":\"2024-01-01 05:25:22\"}, "
                                    + "{\"name\":\"Tyrion Lannister\",\"accompanying_guests\":2,\"time_arrived\":\"2024-01-01 05:25:22\"}]}"
                    )
            )}
    )
    GuestListResponse getArrivedGuestsList();

    @Operation(summary = "Get count of empty seats", description = "Returns the count of available seats")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Number of available seats",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 200 and the count of available seats will be returned",
                                    summary = "available seats",
                                    value = "{\"seats_empty\": 15}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurs",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 500 will be returned if unexpected error occurs on the service side",
                                    summary = "Unexpected error",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 500, " +
                                            "\"error_message\":\"Something goes wrong while calculating available seats\"}"
                            )
                    )}
            )
    })
    SeatsResponse getEmptySeats();
}
