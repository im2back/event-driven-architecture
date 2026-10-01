package github.com.io.im2back.order_service.amqp.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        Integer tableNumber,
        LocalDateTime createdAt
) {
}