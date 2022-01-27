package upgrade.challenge.reservation.v1.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.v1.messaging.eventmessage.CampsiteReservedEvent;
import upgrade.challenge.reservation.v1.service.ReservationService;

@Component
public class EventMessageConsumer {

    private final ReservationService reservationService;

    @Autowired
    public EventMessageConsumer(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @RabbitListener(queues = {"event.campsite.reserved"})
    public void consumeEventMessage(@Payload CampsiteReservedEvent eventMessage) {
        reservationService.confirmReservation(eventMessage.getReservationId());
    }
}
