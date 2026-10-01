package github.com.io.im2back.order_service.amqp.factory.model;

import github.com.io.im2back.order_service.entities.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusUpdatedEvent(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        Integer tableNumber,
        OrderStatus status,
        LocalDateTime createdAt
) {
}