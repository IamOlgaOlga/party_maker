package uk.co.imperatives.exercise.util;

import uk.co.imperatives.exercise.dto.GuestFullInfoResponse;
import uk.co.imperatives.exercise.repository.data.Guest;


public interface GuestToGuestFullInfoResponseConverter {

    public default GuestFullInfoResponse convert(Guest guest){
        return new GuestFullInfoResponse(guest.getName(),
                guest.getTableNumber(), guest.getAccompanyingGuests());
    }
}
