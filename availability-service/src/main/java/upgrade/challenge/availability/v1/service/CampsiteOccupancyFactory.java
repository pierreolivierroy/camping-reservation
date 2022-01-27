package upgrade.challenge.availability.v1.service;

import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationCreatedEvent;

@Component
public class CampsiteOccupancyFactory {

    public CampsiteOccupancy buildCampsiteOccupancy(final ReservationCreatedEvent reservationCreatedEvent) {
        return CampsiteOccupancy.builder()
                .reservationId(reservationCreatedEvent.getReservationId())
                .arrivalDate(reservationCreatedEvent.getArrivalDate())
                .departureDate(reservationCreatedEvent.getDepartureDate())
                .build();
    }
}
