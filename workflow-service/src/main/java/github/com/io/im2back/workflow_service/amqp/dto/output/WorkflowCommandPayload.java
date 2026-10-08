package github.com.io.im2back.workflow_service.amqp.dto.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowCommandPayload<T>(
        UUID eventId,
        String eventType,
        Long orderId,
        String orderNumber,
        Long version,
        LocalDateTime createdAt,
        T eventsData
) {
}