package github.com.io.im2back.order_service.amqp.publisher;



import github.com.io.im2back.order_service.amqp.event.OrderCreatedEvent;
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

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                exchange,
                orderCreatedRoutingKey,
                event
        );
    }
}