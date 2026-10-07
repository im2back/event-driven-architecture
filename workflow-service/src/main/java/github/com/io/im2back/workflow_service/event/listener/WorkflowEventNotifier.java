package github.com.io.im2back.workflow_service.event.listener;


import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;

import github.com.io.im2back.workflow_service.event.dto.WorkflowApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowEventNotifier {

    private final ApplicationEventPublisher eventPublisher;

    public WorkflowEventNotifier(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notify(WorkflowEventPayload<? extends WorkflowEventsData> event, List<WorkflowTransitionAction> actions) {
        eventPublisher.publishEvent(new WorkflowApplicationEvent(event, actions));
    }
}