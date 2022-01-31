package upgrade.challenge.reservation.v1.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upgrade.challenge.reservation.domain.EventType;
import upgrade.challenge.reservation.v1.messaging.eventmessage.ReservationDateSelectionEvent;

import java.util.Map;

@Component
public class EventMessagePublisher {

    private final ObjectMapper objectMapper;
    private final Map<EventType, Queue> queuesByEventType;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventMessagePublisher(ObjectMapper objectMapper,
                                 Map<EventType, Queue> queuesByEventType,
                                 RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.queuesByEventType = queuesByEventType;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SneakyThrows
    public void publishEvent(final String eventMessage, final EventType eventType) {
        rabbitTemplate.convertAndSend(queuesByEventType.get(eventType).getName(),
                objectMapper.readValue(eventMessage, ReservationDateSelectionEvent.class));
    }
}
