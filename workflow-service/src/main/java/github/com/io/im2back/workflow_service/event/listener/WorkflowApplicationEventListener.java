package github.com.io.im2back.workflow_service.event.listener;

import github.com.io.im2back.workflow_service.amqp.dto.output.WorkflowCommandPayload;
import github.com.io.im2back.workflow_service.amqp.factory.WorkflowCommandFactory;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;
import github.com.io.im2back.workflow_service.event.dto.WorkflowApplicationEvent;
import github.com.io.im2back.workflow_service.service.OutboxEventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class WorkflowApplicationEventListener {

    private final WorkflowCommandFactory workflowCommandFactory;
    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;

    public WorkflowApplicationEventListener(WorkflowCommandFactory workflowCommandFactory, OutboxEventService outboxEventService, ObjectMapper objectMapper) {
        this.workflowCommandFactory = workflowCommandFactory;
        this.outboxEventService = outboxEventService;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(WorkflowApplicationEvent event) {
        for (WorkflowTransitionAction action : event.actions()) {
            WorkflowCommandPayload<?> command = workflowCommandFactory.create(
                    action,
                    event.event(),
                    event.version()
            );

            try {
                String payload = objectMapper.writeValueAsString(command);

                outboxEventService.savePending(
                        command.eventId(),
                        action.getActionType(),
                        command.orderId(),
                        payload
                );
            } catch (JacksonException e) {
                throw new RuntimeException("Error serializing workflow command", e);
            }
        }
    }
}