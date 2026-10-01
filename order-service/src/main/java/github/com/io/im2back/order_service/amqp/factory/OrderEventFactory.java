package github.com.io.im2back.order_service.amqp.factory;
import github.com.io.im2back.order_service.amqp.factory.model.OrderCreatedEvent;
import github.com.io.im2back.order_service.amqp.factory.model.OrderStatusUpdatedEvent;
import github.com.io.im2back.order_service.entity.Order;
import github.com.io.im2back.order_service.event.model.OrderEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderEventFactory {

    public Object from(Order order, OrderEventType eventType) {

        return switch (eventType) {
            case CREATED -> created(order);
            case STATUS_UPDATED -> statusUpdated(order);
        };
    }

    private OrderCreatedEvent created(Order order) {

        return new OrderCreatedEvent(
                UUID.randomUUID(),
                "OrderCreated",
                order.getId(),
                order.getOrderNumber(),
                order.getTableNumber(),
                LocalDateTime.now()
        );
    }

    private OrderStatusUpdatedEvent statusUpdated(Order order) {

        return new OrderStatusUpdatedEvent(
                UUID.randomUUID(),
                "OrderStatusUpdated",
                order.getId(),
                order.getOrderNumber(),
                order.getTableNumber(),
                order.getStatus(),
                LocalDateTime.now()
        );
    }
}