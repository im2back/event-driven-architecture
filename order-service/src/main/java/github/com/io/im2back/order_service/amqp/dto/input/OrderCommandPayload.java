package github.com.io.im2back.order_service.amqp.dto.input;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCommandPayload<T>(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        Long version,
        LocalDateTime createdAt,
        T eventsData
) {
}