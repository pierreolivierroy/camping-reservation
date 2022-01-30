package upgrade.challenge.availability.v1.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.CampsiteOccupancy;
import upgrade.challenge.availability.exception.ValidationException;
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
    public void consumeReservationCancelledEventMessage(@Payload ReservationCancelledEvent eventMessage) {
        campsiteOccupancyService.cancel(eventMessage.getReservationId());
    }

    @RabbitListener(queues = {"event.reservation.created"})
    public void consumeReservationDateSelectionEventMessage(@Payload ReservationDateSelectionEvent eventMessage) {
        try {
            campsiteOccupancyService.create(buildCampsiteOccupancy(eventMessage));
        } catch (ValidationException e) {
            // TODO: 2022-01-29 Replace try/catch with proper configuration
        }
    }

    @RabbitListener(queues = {"event.reservation.modified"})
    public void consumeReservationModifiedEventMessage(@Payload ReservationDateSelectionEvent eventMessage) {
        try {
            campsiteOccupancyService.updateOccupancyDates(eventMessage.getReservationId(), buildCampsiteOccupancy(eventMessage));
        } catch (ValidationException e) {
            // TODO: 2022-01-29 Replace try/catch with proper configuration
        }
    }

    private CampsiteOccupancy buildCampsiteOccupancy(final ReservationDateSelectionEvent reservationDateSelectionEvent) {
        return CampsiteOccupancy.builder()
                .reservationId(reservationDateSelectionEvent.getReservationId())
                .arrivalDate(reservationDateSelectionEvent.getArrivalDate())
                .departureDate(reservationDateSelectionEvent.getDepartureDate())
                .build();
    }
}
