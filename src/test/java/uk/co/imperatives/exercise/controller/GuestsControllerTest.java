package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.exception.ExerciseServiceException;
import uk.co.imperatives.exercise.repository.data.Guest;
import uk.co.imperatives.exercise.service.GuestService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for PartyMakerController controller.
 */
@WebMvcTest(GuestsController.class)
public class GuestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    /**
     * This test checks the POST method /guest_list/{name} of PartyMakerController controller.
     * In positive case it should return status 200 and response body with a name of guest.
     */
    @Test
    public void givenCorrectPostGuestRequest_Return200AndName() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, 1, 2);
        String guestName = "Jon Snow";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(guestName)));
    }

    /**
     * This test checks the POST method /guest_list/{name} of PartyMakerController controller.
     * In negative case (table number is -1) it should return status 400 and response body with error.
     */
    @Test
    public void givenNegativeTableInPostGuestRequest_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, -1, 2);
        String guestName = "Jon Snow";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Request validation error")))
                .andExpect(content().string(containsString("Table number value must be more than 0")));
    }

    /**
     * This test checks the POST method /guest_list/{name} of PartyMakerController controller.
     * In negative case (accompanying guests number is not given) it should return status 400 and response body with error.
     */
    @Test
    public void givenNegativeTableInPostGuestRequestNotGiven_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, 2, null);
        String guestName = "Jon Snow";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Request validation error")))
                .andExpect(content().string(containsString("Accompanying guests field must not be null")));
    }

    /**
     * This test checks the POST method /guest_list/{name} of PartyMakerController controller.
     * In negative case (table number is -1) it should return status 400 and response body with error.
     */
    @Test
    public void givenNegativeAccompanyingGuestsCountInPostGuestRequest_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, 1,-2);
        String guestName = "Jon Snow";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Request validation error")))
                .andExpect(content().string(containsString("Accompanying Guests must be more than 0")));
    }

    /**
     * This test checks the GET method /guest_list of PartyMakerController controller.
     * In positive case it should return status 200 and response body with a list of guest.
     */
    @Test
    public void givenCorrectGetGuestListRequest_Return200AndGuestList() throws Exception {
        var guest1 = new Guest("Jon Snow", 1, 0);
        var guest2 = new Guest("Arya Stark", 1, 7);
        var guest3 = new Guest("Tyrion Lannister", 2, 2);
        List<Guest> guestList = new ArrayList<>();
        guestList.add(guest1);
        guestList.add(guest2);
        guestList.add(guest3);
        given(guestService.getGuestList()).willReturn(guestList);

        String resultBodyResponse = "{\"guests\":[" +
                "{\"name\":\"Jon Snow\",\"table\":1,\"accompanying_guests\":0}," +
                "{\"name\":\"Arya Stark\",\"table\":1,\"accompanying_guests\":7}," +
                "{\"name\":\"Tyrion Lannister\",\"table\":2,\"accompanying_guests\":2}]}";
        mockMvc.perform(get("/guest_list"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultBodyResponse));
    }

    /**
     * This test checks the PUT /guests/{name} method.
     * A guest may arrive with his friends that are not the size indicated at the guest list.
     * Table is expected to have space for the extras, allow them to come -- returns the guest's name and status 200.
     */
    @Test
    public void givenCorrectPutGuestRequest_Return200AndGuestName() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, null, 2);
        String guestName = "Jon Snow";
        given(guestService.checkInGuest(anyString(), anyInt())).willReturn(guestName);

        mockMvc.perform(put("/guests/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(guestName)));
    }

    /**
     * This test checks the PUT /guests/{name} method.
     * A guest may arrive with his friends that are not the size indicated at the guest list.
     * Table is expected to have space for the extras, allow them to come -- returns the guest's name and status 200.
     */
    @Test
    public void givenCorrectPutGuestRequest_NotAvailableSpace_ReturnStatus400() throws Exception {
        GuestRequest guestRequest = new GuestRequest(null, null, 2);
        String guestName = "Jon Snow";
        given(guestService.checkInGuest(anyString(), anyInt())).willThrow(ExerciseServiceException.class);

        mockMvc.perform(put("/guests/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest());
    }
}
