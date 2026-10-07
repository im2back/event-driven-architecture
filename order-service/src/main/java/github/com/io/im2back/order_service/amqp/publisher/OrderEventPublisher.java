package github.com.io.im2back.order_service.amqp.publisher;

import github.com.io.im2back.order_service.event.model.OrderEventType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

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

    public void publish(String payload, OrderEventType eventType) {
        String routingKey = getRoutingKey(eventType);

        Message message = MessageBuilder.withBody(payload.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();

        rabbitTemplate.send(exchange, routingKey, message);
    }

    private String getRoutingKey(OrderEventType eventType) {
        String routingKey = switch (eventType) {
            case ORDER_CREATED -> orderCreatedRoutingKey;
            case STATUS_UPDATED -> orderStatusUpdatedRoutingKey;
        };
        return routingKey;
    }

}