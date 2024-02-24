package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.imperatives.exercise.dto.GuestRequest;
import uk.co.imperatives.exercise.service.GuestService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for PartyMakerController controller.
 */
@WebMvcTest(PartyMakerController.class)
public class PartyMakerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;
    /**
     * This test checks the POST method /guest_list/{name} of PartyMakerController controller.
     * In positive case it should return status 200 and response body with a name of guest.
     */
    @Test
    public void addGuest_Return200AndName() throws Exception {
        GuestRequest guestRequest = new GuestRequest(1,2);
        String guestName = "GuestName";
        given(guestService.addGuest(anyString(), anyInt(), anyInt())).willReturn(guestName);

        mockMvc.perform(post("/guest_list/{name}", guestName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(guestName)));
    }

}
