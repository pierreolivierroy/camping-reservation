package upgrade.challenge.reservation.v1.v1.web.adapter;

import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.SuccessfulReservationDto;

import java.time.Instant;

import static org.springframework.util.Assert.notNull;

@Component
public class ReservationAdapter {

    public SuccessfulReservationDto reserveCampsite(final ReservationDto reservationDto) {
        notNull(reservationDto, "The reservationDto is mandatory.");

        return buildSuccessfulReservationDto(reservationDto.getArrivalDate(), reservationDto.getDepartureDate());
    }

    private SuccessfulReservationDto buildSuccessfulReservationDto(final Instant arrivalDate,
                                                                   final Instant departureDate) {
        return SuccessfulReservationDto.builder()
                .reservationId("temporary-id")
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .build();
    }
}
