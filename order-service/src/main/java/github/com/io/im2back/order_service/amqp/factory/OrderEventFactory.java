package github.com.io.im2back.order_service.amqp.factory;

import github.com.io.im2back.order_service.amqp.factory.model.OrderCreatedEventsData;
import github.com.io.im2back.order_service.amqp.factory.model.OrderEventPayload;
import github.com.io.im2back.order_service.entities.order.Order;
import github.com.io.im2back.order_service.event.model.OrderEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderEventFactory {

    public OrderEventPayload<OrderCreatedEventsData> from(Order order, OrderEventType eventType) {

        OrderCreatedEventsData eventsData = new OrderCreatedEventsData(
                order.getTableNumber(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getStatus()
        );

        return new OrderEventPayload<>(
                UUID.randomUUID(),
                eventType.name(),
                order.getId(),
                order.getOrderNumber(),
                LocalDateTime.now(),
                eventsData
        );
    }
}