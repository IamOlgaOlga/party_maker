package uk.co.imperatives.exercise.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.repository.JpaGuestRepository;
import uk.co.imperatives.exercise.repository.data.Guest;

@AllArgsConstructor
@Service
public class GuestService {

    private JpaGuestRepository guestRepository;

    public String addGuest(String name, int tableNumber, int accompanyingGuests){
        Guest guest = new Guest();
        guest.setName(name);
        guest.setTableNumber(tableNumber);
        guest.setAccompanyingGuests(accompanyingGuests);
        return guestRepository.save(guest);
    }
}
