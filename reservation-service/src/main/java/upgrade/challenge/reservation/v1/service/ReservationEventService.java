package upgrade.challenge.reservation.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upgrade.challenge.reservation.domain.ReservationEvent;
import upgrade.challenge.reservation.repository.ReservationEventRepository;
import upgrade.challenge.reservation.v1.messaging.publisher.EventMessagePublisher;

import java.sql.SQLException;

@Service
public class ReservationEventService {

    private final EventMessagePublisher eventMessagePublisher;
    private final ReservationEventRepository reservationEventRepository;

    @Autowired
    public ReservationEventService(EventMessagePublisher eventMessagePublisher,
                                   ReservationEventRepository reservationEventRepository) {
        this.eventMessagePublisher = eventMessagePublisher;
        this.reservationEventRepository = reservationEventRepository;
    }

    // TODO: 2022-01-26 Replace with outbox table pattern
    @Transactional(rollbackFor = SQLException.class)
    public void create(final ReservationEvent reservationEvent) {
        reservationEventRepository.save(reservationEvent);
        eventMessagePublisher.publishEvent(reservationEvent.getPayload(), reservationEvent.getEventType());
    }
}
