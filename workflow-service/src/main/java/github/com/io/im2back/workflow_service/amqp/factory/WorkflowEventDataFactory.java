package github.com.io.im2back.workflow_service.amqp.factory;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.amqp.dto.input.order.OrderCreatedEventsData;
import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
public class WorkflowEventDataFactory {

    private final ObjectMapper objectMapper;

    public WorkflowEventDataFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WorkflowEventPayload<? extends WorkflowEventsData> create(WorkflowEventPayload<?> event) {
        WorkflowEventType eventType = WorkflowEventType.valueOf(event.eventType());

        return switch (eventType) {
            case ORDER_CREATED -> new WorkflowEventPayload<>(
                    event.eventId(),
                    event.eventType(),
                    event.orderId(),
                    event.orderNumber(),
                    event.createdAt(),
                    objectMapper.convertValue(event.eventsData(), OrderCreatedEventsData.class)
            );

            default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
        };
    }
}