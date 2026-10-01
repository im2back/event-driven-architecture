package github.com.io.im2back.order_service.amqp.publisher;

import github.com.io.im2back.order_service.event.model.OrderEventType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.domain-events}")
    private String exchange;

    @Value("${messaging.routing-key.order-created}")
    private String orderCreatedRoutingKey;

    @Value("${messaging.routing-key.order-status-updated}")
    private String orderStatusUpdatedRoutingKey;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Object event, OrderEventType eventType) {

        String routingKey = switch (eventType) {
            case CREATED -> orderCreatedRoutingKey;
            case STATUS_UPDATED -> orderStatusUpdatedRoutingKey;
        };

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}