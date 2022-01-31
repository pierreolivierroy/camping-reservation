package upgrade.challenge.availability.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import upgrade.challenge.availability.domain.EventType;

import java.util.Map;

@Configuration
public class RabbitPublisherConfiguration {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(final ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, final ObjectMapper objectMapper) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter(objectMapper));

        return rabbitTemplate;
    }

    @Bean
    public Queue campsiteReservedQueue() {
        return new Queue("event.campsite.reserved", true);
    }

    @Bean
    public Queue reservationRollbackQueue() {
        return new Queue("event.reservation.rollback", true);
    }

    @Bean
    public Map<EventType, Queue> queuesByEventType() {
        return Map.of(
                EventType.CAMPSITE_RESERVED, campsiteReservedQueue(),
                EventType.CAMPSITE_RESERVATION_ROLLBACK, reservationRollbackQueue()
        );
    }
}
