package upgrade.challenge.availability.v1.messaging.publisher;

import lombok.SneakyThrows;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upgrade.challenge.availability.domain.EventType;

import java.util.Map;

@Component
public class EventMessagePublisher {

    private final Map<EventType, Queue> queuesByEventType;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventMessagePublisher(Map<EventType, Queue> queuesByEventType,
                                 RabbitTemplate rabbitTemplate) {
        this.queuesByEventType = queuesByEventType;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SneakyThrows
    public void publishEvent(final Object eventMessage, final EventType eventType) {
        rabbitTemplate.convertAndSend(queuesByEventType.get(eventType).getName(), eventMessage);
    }
}
