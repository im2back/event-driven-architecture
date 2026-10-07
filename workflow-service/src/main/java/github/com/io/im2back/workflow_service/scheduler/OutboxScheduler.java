package github.com.io.im2back.workflow_service.scheduler;

import github.com.io.im2back.workflow_service.amqp.publisher.WorkflowCommandPublisher;
import github.com.io.im2back.workflow_service.entities.outbox.OutboxEvent;
import github.com.io.im2back.workflow_service.service.OutboxEventService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxScheduler {

    private final OutboxEventService outboxEventService;
    private final WorkflowCommandPublisher workflowCommandPublisher;

    public OutboxScheduler(OutboxEventService outboxEventService, WorkflowCommandPublisher workflowCommandPublisher) {
        this.outboxEventService = outboxEventService;
        this.workflowCommandPublisher = workflowCommandPublisher;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay}")
    public void processPendingEvents() {
        List<OutboxEvent> events = outboxEventService.findPending();

        for (OutboxEvent event : events) {
            workflowCommandPublisher.publish(event.getPayload(), event.getActionType());
        }

        if (!events.isEmpty()) {
            outboxEventService.markAsPublished(events);
        }
    }
}