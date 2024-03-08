package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.imperatives.exercise.dto.TableRequest;
import uk.co.imperatives.exercise.repository.data.Table;
import uk.co.imperatives.exercise.service.TableService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for TableController controller.
 */
@WebMvcTest(TableController.class)
public class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableService tableService;

    /**
     * This test checks the POST method /table of TableController controller.
     * In positive case it should return status 201 and response body with a table ID.
     */
    @Test
    public void givenCorrectPostTableRequest_Return201AndTableID() throws Exception {
        TableRequest tableRequest = new TableRequest(1, 2);
        given(tableService.addTable(1, 2)).willReturn(1);

        mockMvc.perform(post("/table")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tableRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("{\"table_id\":1}")));
    }

    /**
     * This test checks the POST method /table of TableController controller.
     * In negative case (capacity < 0) it should return status 400 and error message.
     */
    @Test
    public void givenIncorrectPostTableRequest_Return400AndTableID() throws Exception {
        TableRequest tableRequest = new TableRequest(1, -2);

        mockMvc.perform(post("/table")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tableRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Request validation error")))
                .andExpect(content().string(containsString("Capacity value must be positive or equals 0")));
    }

    /**
     * This test checks the GET method /tables_list of TableController controller.
     * In positive case it should return status 200 and response body with tables list.
     */
    @Test
    public void givenCorrectGetTableListRequest_Return200AndTableID() throws Exception {
        List<Table> tablesList = new ArrayList<>();
        tablesList.add(new Table(1,2));
        tablesList.add(new Table(2,3));
        given(tableService.getTablesList()).willReturn(tablesList);
        //
        String resultBodyResponse = "{\"tables_list\":[" +
                "{\"table_id\":1,\"capacity\":2}," +
                "{\"table_id\":2,\"capacity\":3}]}";
        mockMvc.perform(get("/tables_list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(resultBodyResponse)));
    }

    /**
     * This test checks the PUT method /table/{id} of TableController controller.
     * In positive case it should return status 200 and response body with table id.
     */
    @Test
    public void givenCorrectPutTableRequest_Return200AndTableInfo() throws Exception {
        int tableId = 2;
        TableRequest tableRequest = new TableRequest(null,5);
        given(tableService.updateTable(tableId, 5)).willReturn(tableId);
        //
        mockMvc.perform(put("/table/{id}", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tableRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"table_id\":2}")));
    }

    /**
     * This test checks the PUT method /table/{id} of TableController controller.
     * In negative case (capacity < 0) it should return status 400 and error message.
     */
    @Test
    public void givenIncorrectPutTableRequest_Return400AndErrorMessage() throws Exception {
        int tableId = 2;
        TableRequest tableRequest = new TableRequest(null, -5);
        //
        mockMvc.perform(put("/table/{id}", tableId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tableRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Request validation error")))
                .andExpect(content().string(containsString("Capacity value must be positive or equals 0")));
    }
}
