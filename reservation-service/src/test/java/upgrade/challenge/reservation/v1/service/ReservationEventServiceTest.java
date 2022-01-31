package upgrade.challenge.reservation.v1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upgrade.challenge.reservation.domain.EventType;
import upgrade.challenge.reservation.domain.ReservationEvent;
import upgrade.challenge.reservation.repository.ReservationEventRepository;
import upgrade.challenge.reservation.v1.messaging.publisher.EventMessagePublisher;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationEventServiceTest {

    private ReservationEventService testee;

    @Mock
    private EventMessagePublisher eventMessagePublisher;

    @Mock
    private ReservationEventRepository reservationEventRepository;

    private ReservationEvent reservationEvent;

    @BeforeEach
    void setUp() {
        testee = new ReservationEventService(eventMessagePublisher, reservationEventRepository);
        reservationEvent = buildReservationEvent();
    }

    @Test
    void create() {
        testee.create(reservationEvent);

        verify(reservationEventRepository).save(reservationEvent);
        verify(eventMessagePublisher).publishEvent("payload", EventType.RESERVATION_CREATED);
    }

    private ReservationEvent buildReservationEvent() {
        return ReservationEvent.builder()
                .payload("payload")
                .aggregateId(123456789L)
                .aggregateType("Reservation")
                .eventType(EventType.RESERVATION_CREATED)
                .build();
    }
}
