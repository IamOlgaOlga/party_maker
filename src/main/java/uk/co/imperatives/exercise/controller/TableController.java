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
import uk.co.imperatives.exercise.dto.TableListResponse;
import uk.co.imperatives.exercise.dto.TableRequest;
import uk.co.imperatives.exercise.dto.TableResponse;

@Tag(name = "Tables Controller", description = "Controller to manage tables")
public interface TableController {

    @Operation(summary = "Add a new table",
            description = "Add a new table for the party")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created a new table",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 201 will be returned if a new table was created",
                                    summary = "New table was created",
                                    value = "{\"table_id\":1}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected service error",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 500 will be returned in case any error while saving a new table",
                                    summary = "Unexpected error",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 500, " +
                                            "\"error_message\":\"An error occurs while saving a new table\"}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "409", description = "Table already exists",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 409 will be returned if the table already exists",
                                    summary = "Table already exists",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 409, " +
                                            "\"error_message\":\"Table with ID = 1 already exists\"}"
                            )
                    )}
            )
    })
    ResponseEntity<TableResponse> addTable(
            @RequestBody(description = "Table information", required = true, content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestRequest.class),
                    examples = @ExampleObject(
                            name = "A new table with capacity = 10",
                            summary = "Table with capacity 10",
                            value = "{\"table_id\":1, \"capacity\": 10}"
                    ))
            )
            TableRequest tableRequest);

    @Operation(summary = "Get the table list", description = "Provides a list with information about all tables")
    @ApiResponse(
            responseCode = "200",
            description = "A tables list",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestListResponse.class),
                    examples = @ExampleObject(
                            name = "Status 200 and a list with information about tables will be returned",
                            summary = "tables list",
                            value = " {\"tables_list\":[" +
                                    "{\"table_id\":1, \"capacity\":5}, " +
                                    "{\"table_id\":2, \"capacity\":10}, " +
                                    "{\"table_id\":3, \"capacity\":20}]}"
                    )
            )}
    )
    TableListResponse getTablesList();

    @Operation(summary = "Update table",
            description = "Update table capacity")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Update table",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GuestResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 200 will be returned if table was updated",
                                    summary = "Table was updated",
                                    value = "{\"table_id\":1}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected service error",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 500 will be returned in case any error while saving a new table",
                                    summary = "Unexpected error",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 500, " +
                                            "\"error_message\":\"Error in DB while table capacity update\"}"
                            )
                    )}
            ),
            @ApiResponse(responseCode = "404", description = "Table not found",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Status 404 will be returned if requested table was not found",
                                    summary = "Table not found",
                                    value = "{\"timestamp\": \"2024-01-01 05:20:22\", \"status\": 404, " +
                                            "\"error_message\":\"Table with ID = 1 does not exist\"}"
                            )
                    )}
            )
    })
    TableResponse updateTable(
            @Parameter(description = "Table ID", required = true, example = "1")
            Integer id,
            @RequestBody(description = "Table information", required = true, content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuestRequest.class),
                    examples = @ExampleObject(
                            name = "A new table with capacity = 10",
                            summary = "Table with capacity 10",
                            value = "{\"capacity\": 10}"
                    ))
            )
            TableRequest tableRequest);
}
