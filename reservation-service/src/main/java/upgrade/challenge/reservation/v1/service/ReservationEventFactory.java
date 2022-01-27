package upgrade.challenge.reservation.v1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.domain.EventType;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationEvent;
import upgrade.challenge.reservation.v1.messaging.eventmessage.ReservationCreatedEvent;

@Component
public class ReservationEventFactory {

    private final ObjectMapper objectMapper;

    public ReservationEventFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReservationEvent buildReservationEvent(final Reservation reservation, final EventType eventType) {
        return ReservationEvent.builder()
                .aggregateId(reservation.getId())
                .eventType(eventType)
                .payload(buildPayload(reservation, eventType))
                .build();
    }

    @SneakyThrows
    private String buildPayload(final Reservation reservation, final EventType eventType) {
        return switch (eventType) {
            case RESERVATION_CREATED -> objectMapper.writeValueAsString(buildReservationCreatedEvent(reservation));
        };
    }

    private ReservationCreatedEvent buildReservationCreatedEvent(final Reservation reservation) {
        return ReservationCreatedEvent.builder()
                .reservationId(reservation.getId())
                .arrivalDate(reservation.getArrivalDate())
                .departureDate(reservation.getDepartureDate())
                .build();
    }
}
