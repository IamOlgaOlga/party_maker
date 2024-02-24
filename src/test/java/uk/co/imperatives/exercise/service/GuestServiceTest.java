package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for GuestService
 */
@SpringBootTest
public class GuestServiceTest {

    @Mock
    private JpaGuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    /**
     * Given guestRepository will return test guest name.
     * Call method addGuest() with test parameters.
     * Check that guest service return test guest name.
     */
    @Test
    public void givenGuestInfoSaveToDbAndReturnName(){
        String guestName = "Guest Name";
        given(guestRepository.save(any())).willReturn(guestName);
        assertThat(guestService.addGuest(guestName, 1, 1 ), equalTo(guestName));
    }
}
