package github.com.io.im2back.workflow_service.amqp.factory;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.amqp.dto.input.order.OrderCreatedEventsData;
import github.com.io.im2back.workflow_service.amqp.dto.output.WorkflowCommandPayload;
import github.com.io.im2back.workflow_service.amqp.dto.output.order.UpdateOrderStatusEventsData;
import github.com.io.im2back.workflow_service.amqp.dto.output.payment.ProcessPaymentEventsData;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class WorkflowCommandFactory {

    public WorkflowCommandPayload<?> create(WorkflowTransitionAction action, WorkflowEventPayload<? extends WorkflowEventsData> event) {
        return switch (action.getActionType()) {

            case PROCESS_PAYMENT -> {
                if (!(event.eventsData() instanceof OrderCreatedEventsData data)) {
                    throw new IllegalArgumentException("Invalid eventsData for PROCESS_PAYMENT");
                }

                yield new WorkflowCommandPayload<>(
                        UUID.randomUUID(),
                        action.getActionType().name(),
                        event.orderId(),
                        event.orderNumber(),
                        LocalDateTime.now(),
                        new ProcessPaymentEventsData(data.totalAmount(), data.currency())
                );
            }

            case UPDATE_ORDER_STATUS -> new WorkflowCommandPayload<>(
                    UUID.randomUUID(),
                    action.getActionType().name(),
                    event.orderId(),
                    event.orderNumber(),
                    LocalDateTime.now(),
                    new UpdateOrderStatusEventsData(action.getActionValue())
            );

            default -> throw new IllegalArgumentException("Unsupported action type: " + action.getActionType());
        };
    }
}