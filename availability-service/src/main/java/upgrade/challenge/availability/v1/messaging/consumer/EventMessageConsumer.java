package upgrade.challenge.availability.v1.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.v1.messaging.eventmessage.ReservationCreatedEvent;
import upgrade.challenge.availability.v1.service.CampsiteOccupancyFactory;
import upgrade.challenge.availability.v1.service.CampsiteOccupancyService;

@Component
public class EventMessageConsumer {

    private final CampsiteOccupancyFactory campsiteOccupancyFactory;
    private final CampsiteOccupancyService campsiteOccupancyService;

    @Autowired
    public EventMessageConsumer(CampsiteOccupancyFactory campsiteOccupancyFactory,
                                CampsiteOccupancyService campsiteOccupancyService) {
        this.campsiteOccupancyFactory = campsiteOccupancyFactory;
        this.campsiteOccupancyService = campsiteOccupancyService;
    }

    @RabbitListener(queues = {"event.reservation.created"})
    public void consumeEventMessage(@Payload ReservationCreatedEvent eventMessage) {
        campsiteOccupancyService.create(campsiteOccupancyFactory.buildCampsiteOccupancy(eventMessage));
    }
}
