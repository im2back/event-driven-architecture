package github.com.io.im2back.workflow_service.amqp.listener;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.amqp.factory.WorkflowEventDataFactory;
import github.com.io.im2back.workflow_service.service.WorkflowService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkflowEventListener {

    private final WorkflowService workflowService;
    private final WorkflowEventDataFactory workflowEventDataFactory;
    private final MetadataStoreSelector metadataStoreSelector;

    public WorkflowEventListener(WorkflowService workflowService, WorkflowEventDataFactory workflowEventDataFactory, MetadataStoreSelector metadataStoreSelector) {
        this.workflowService = workflowService;
        this.workflowEventDataFactory = workflowEventDataFactory;
        this.metadataStoreSelector = metadataStoreSelector;
    }

    @Transactional
    @RabbitListener(queues = "${messaging.queue.workflow-events}")
    public void consume(WorkflowEventPayload<?> event) {
        Message<?> message = MessageBuilder.withPayload(event).build();

        if (!metadataStoreSelector.accept(message)) {
            return;
        }

        WorkflowEventPayload<? extends WorkflowEventsData> typedEvent = workflowEventDataFactory.create(event);
        workflowService.process(typedEvent);
    }
}