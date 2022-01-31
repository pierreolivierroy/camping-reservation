package upgrade.challenge.reservation.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.reservation.domain.EventType;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationEvent;
import upgrade.challenge.reservation.v1.messaging.eventmessage.ReservationCancelledEvent;
import upgrade.challenge.reservation.v1.messaging.eventmessage.ReservationDateSelectionEvent;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationEventFactoryTest {

    private static final Long RESERVATION_ID = 1234556789L;

    private ReservationEventFactory testee;

    @Mock
    private ObjectMapper objectMapper;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        testee = new ReservationEventFactory(objectMapper);
        reservation = buildReservation();
    }

    @Test
    void buildPayload_withReservationCancelledEventType() throws JsonProcessingException {
        final ReservationCancelledEvent cancelledEvent = buildReservationCancelledEvent(reservation);
        final ReservationEvent expected = buildReservationEvent(EventType.RESERVATION_CANCELLED);

        when(objectMapper.writeValueAsString(cancelledEvent)).thenReturn("payload");

        final ReservationEvent actual = testee.buildReservationEvent(reservation, EventType.RESERVATION_CANCELLED);

        assertThat(actual).isEqualTo(expected);

        verify(objectMapper).writeValueAsString(cancelledEvent);
    }

    @Test
    void buildPayload_withReservationCreatedEventType() throws JsonProcessingException {
        final ReservationDateSelectionEvent dateSelectionEvent = buildReservationCreatedEvent(reservation);
        final ReservationEvent expected = buildReservationEvent(EventType.RESERVATION_CREATED);

        when(objectMapper.writeValueAsString(dateSelectionEvent)).thenReturn("payload");

        final ReservationEvent actual = testee.buildReservationEvent(reservation, EventType.RESERVATION_CREATED);

        assertThat(actual).isEqualTo(expected);

        verify(objectMapper).writeValueAsString(dateSelectionEvent);
    }

    @Test
    void buildPayload_withReservationModifiedEventType() throws JsonProcessingException {
        final ReservationDateSelectionEvent dateSelectionEvent = buildReservationCreatedEvent(reservation);
        final ReservationEvent expected = buildReservationEvent(EventType.RESERVATION_MODIFIED);

        when(objectMapper.writeValueAsString(dateSelectionEvent)).thenReturn("payload");

        final ReservationEvent actual = testee.buildReservationEvent(reservation, EventType.RESERVATION_MODIFIED);

        assertThat(actual).isEqualTo(expected);

        verify(objectMapper).writeValueAsString(dateSelectionEvent);
    }

    private Reservation buildReservation() {
        return Reservation.builder()
                .id(RESERVATION_ID)
                .arrivalDate(Instant.now())
                .departureDate(Instant.now())
                .build();
    }

    private ReservationEvent buildReservationEvent(final EventType eventType) {
        return ReservationEvent.builder()
                .aggregateId(RESERVATION_ID)
                .aggregateType("Reservation")
                .eventType(eventType)
                .payload("payload")
                .build();
    }

    private ReservationCancelledEvent buildReservationCancelledEvent(final Reservation reservation) {
        return ReservationCancelledEvent.builder()
                .reservationId(reservation.getId())
                .build();
    }

    private ReservationDateSelectionEvent buildReservationCreatedEvent(final Reservation reservation) {
        return ReservationDateSelectionEvent.builder()
                .reservationId(reservation.getId())
                .arrivalDate(reservation.getArrivalDate())
                .departureDate(reservation.getDepartureDate())
                .build();
    }
}
