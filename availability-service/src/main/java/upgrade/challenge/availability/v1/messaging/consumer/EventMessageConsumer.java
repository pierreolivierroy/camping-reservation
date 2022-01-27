package upgrade.challenge.availability.v1.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationCancelledEvent;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationCreatedEvent;
import upgrade.challenge.availability.v1.service.CampsiteOccupancyService;

@Component
public class EventMessageConsumer {

    private final CampsiteOccupancyService campsiteOccupancyService;

    @Autowired
    public EventMessageConsumer(CampsiteOccupancyService campsiteOccupancyService) {
        this.campsiteOccupancyService = campsiteOccupancyService;
    }

    @RabbitListener(queues = {"event.reservation.cancelled"})
    public void consumeReservationCreatedEventMessage(@Payload ReservationCancelledEvent eventMessage) {
        campsiteOccupancyService.cancel(eventMessage.getReservationId());
    }

    @RabbitListener(queues = {"event.reservation.created"})
    public void consumeReservationCreatedEventMessage(@Payload ReservationCreatedEvent eventMessage) {
        campsiteOccupancyService.create(buildCampsiteOccupancy(eventMessage));
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final ReservationCreatedEvent reservationCreatedEvent) {
        return CampsiteOccupancy.builder()
                .reservationId(reservationCreatedEvent.getReservationId())
                .arrivalDate(reservationCreatedEvent.getArrivalDate())
                .departureDate(reservationCreatedEvent.getDepartureDate())
                .build();
    }
}
