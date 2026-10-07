package github.com.io.im2back.workflow_service.event.dto;



import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;
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