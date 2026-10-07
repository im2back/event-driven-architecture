package github.com.io.im2back.workflow_service.amqp.listener;


import github.com.io.im2back.workflow_service.amqp.model.WorkflowEventPayload;

import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import github.com.io.im2back.workflow_service.service.WorkflowService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEventListener {

    private final WorkflowService workflowService;

    public WorkflowEventListener(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @RabbitListener(queues = "${messaging.queue.workflow-events}")
    public void consume(WorkflowEventPayload event) {
        workflowService.process(
                event.orderId(),
                WorkflowEventType.valueOf(event.eventType())
        );
    }
}