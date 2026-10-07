package github.com.io.im2back.workflow_service.amqp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowEventPayload(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        Integer tableNumber,
        String status,
        LocalDateTime createdAt
) {
}