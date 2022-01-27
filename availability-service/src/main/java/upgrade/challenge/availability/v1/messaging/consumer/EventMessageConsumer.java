package upgrade.challenge.availability.v1.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationCancelledEvent;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationDateSelectionEvent;
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
    public void consumeReservationCreatedEventMessage(@Payload ReservationDateSelectionEvent eventMessage) {
        campsiteOccupancyService.create(buildCampsiteOccupancy(eventMessage));
    }

    @RabbitListener(queues = {"event.reservation.modified"})
    public void consumeReservationModifiedEventMessage(@Payload ReservationDateSelectionEvent eventMessage) {
        campsiteOccupancyService.updateOccupancyDates(eventMessage.getReservationId(), buildCampsiteOccupancy(eventMessage));
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final ReservationDateSelectionEvent reservationDateSelectionEvent) {
        return CampsiteOccupancy.builder()
                .reservationId(reservationDateSelectionEvent.getReservationId())
                .arrivalDate(reservationDateSelectionEvent.getArrivalDate())
                .departureDate(reservationDateSelectionEvent.getDepartureDate())
                .build();
    }
}
