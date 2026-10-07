package github.com.io.im2back.workflow_service.amqp.listener;


import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.amqp.factory.WorkflowEventDataFactory;
import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import github.com.io.im2back.workflow_service.service.WorkflowService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEventListener {

    private final WorkflowService workflowService;
    private final WorkflowEventDataFactory workflowEventDataFactory;

    public WorkflowEventListener(WorkflowService workflowService, WorkflowEventDataFactory workflowEventDataFactory) {
        this.workflowService = workflowService;
        this.workflowEventDataFactory = workflowEventDataFactory;
    }

    @RabbitListener(queues = "${messaging.queue.workflow-events}")
    public void consume(WorkflowEventPayload<?> event) {

        WorkflowEventPayload<? extends WorkflowEventsData> typedEvent = workflowEventDataFactory.create(event);

        workflowService.process(typedEvent);
    }
}