package github.com.io.im2back.workflow_service.amqp.dto.input;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowEventPayload<T>(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        LocalDateTime createdAt,
        T eventsData
) {
}