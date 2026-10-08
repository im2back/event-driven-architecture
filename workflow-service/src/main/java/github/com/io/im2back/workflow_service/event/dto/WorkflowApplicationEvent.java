package github.com.io.im2back.workflow_service.event.dto;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;

import java.util.List;

public record WorkflowApplicationEvent(
        WorkflowEventPayload<? extends WorkflowEventsData> event,
        List<WorkflowTransitionAction> actions,
        Long version
) {
}