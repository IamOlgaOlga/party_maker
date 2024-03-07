package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.exception.ExerciseServiceBadRequestException;
import uk.co.imperatives.exercise.repository.data.Guest;
import uk.co.imperatives.exercise.service.GuestService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for GuestController controller.
 */
@WebMvcTest(GuestsController.class)
public class GuestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    /**
     * This test checks the POST method /guest_list/{name}
     * In positive case it should return status 200 and response body with a name of guest.
     */
    @Test
    public void givenCorrectPostGuestRequest_Return200AndName() throws Exception {
        GuestRequest guestRequest = new GuestRequest(1, 2);
        String guestName = "Jon Snow";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(guestName)));
    }

    /**
     * This test checks the POST method /guest_list/{name}
     * In negative case (table number is -1) it should return status 400 and response body with error.
     */
    @Test
    public void givenNegativeTableInPostGuestRequest_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(-1, 2);
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
     * This test checks the POST method /guest_list/{name}
     * In negative case (accompanying guests number is not given) it should return status 400 and response body with error.
     */
    @Test
    public void givenAccompanyingGuestsInPostGuestRequestNotGiven_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(2, null);
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
     * This test checks the POST method /guest_list/{name}
     * In negative case (table number is -1) it should return status 400 and response body with error.
     */
    @Test
    public void givenNegativeAccompanyingGuestsCountInPostGuestRequest_Return400AndError() throws Exception {
        GuestRequest guestRequest = new GuestRequest(1,-2);
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
     * This test checks the GET method /guest_list
     * In positive case it should return status 200 and response body with a list of guest.
     * Checks that accompanying guest = total guest (from DB) - 1
     */
    @Test
    public void givenCorrectGetGuestListRequest_Return200AndGuestList() throws Exception {
        var guest1 = new Guest("Jon Snow", 1, 1);
        var guest2 = new Guest("Arya Stark", 1, 7);
        var guest3 = new Guest("Tyrion Lannister", 2, 2);
        List<Guest> guestList = new ArrayList<>();
        guestList.add(guest1);
        guestList.add(guest2);
        guestList.add(guest3);
        given(guestService.getGuestList()).willReturn(guestList);

        String resultBodyResponse = "{\"guests\":[" +
                "{\"name\":\"Jon Snow\",\"table\":1,\"accompanying_guests\":0}," +
                "{\"name\":\"Arya Stark\",\"table\":1,\"accompanying_guests\":6}," +
                "{\"name\":\"Tyrion Lannister\",\"table\":2,\"accompanying_guests\":1}]}";
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
        GuestRequest guestRequest = new GuestRequest(null, 2);
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
        GuestRequest guestRequest = new GuestRequest(null, 2);
        String guestName = "Jon Snow";
        given(guestService.checkInGuest(anyString(), anyInt())).willThrow(ExerciseServiceBadRequestException.class);

        mockMvc.perform(put("/guests/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test for the DELETE /guests/{name} method
     * In positive case controller must return status 200 and guest's name.
     */
    @Test
    public void givenExistedGuest_DeleteGuest_ReturnStatus200AndGuestName() throws Exception{
        String guestName = "Jon Snow";
        given(guestService.delete(anyString())).willReturn(guestName);

        mockMvc.perform(delete("/guests/{name}", guestName))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(guestName)));
    }

    /**
     * Test for the DELETE /guests/{name} method
     * In negative case (guest doesn't exist) controller must return status 400.
     */
    @Test
    public void givenNotExistedGuest_ReturnStatus400() throws Exception{
        String guestName = "Jon Snow";
        given(guestService.delete(anyString())).willThrow(ExerciseServiceBadRequestException.class);

        mockMvc.perform(delete("/guests/{name}", guestName))
                .andExpect(status().isBadRequest());
    }

    /**
     * This test checks the GET method /guests
     * In positive case it should return status 200 and response body with a list of guest.
     * Checks that accompanying guest = total guest (from DB) - 1
     */
    @Test
    public void givenCorrectGetArrivedGuestListRequest_Return200AndGuestList() throws Exception {
        var date1 = new Date();
        var guest1 = new Guest("Jon Snow", 2, date1);
        var date2 = new Date();
        var guest2 = new Guest("Arya Stark", 2, date2);
        var date3 = new Date();
        var guest3 = new Guest("Tyrion Lannister", 3, date3);
        List<Guest> guestList = new ArrayList<>();
        guestList.add(guest1);
        guestList.add(guest2);
        guestList.add(guest3);
        given(guestService.getArrivedGuestList()).willReturn(guestList);

        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        var resultBodyResponse = "{\"guests\":[" +
                "{\"name\":\"Jon Snow\",\"accompanying_guests\":1,\"time_arrived\":\"" + dateFormat.format(date1) + "\"}," +
                "{\"name\":\"Arya Stark\",\"accompanying_guests\":1,\"time_arrived\":\"" + dateFormat.format(date2) + "\"}," +
                "{\"name\":\"Tyrion Lannister\",\"accompanying_guests\":2,\"time_arrived\":\"" + dateFormat.format(date3) + "\"}]}";
        mockMvc.perform(get("/guests"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultBodyResponse));
    }
}
