package github.com.io.im2back.workflow_service.service;


import github.com.io.im2back.workflow_service.entities.outbox.OutboxEvent;

import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowActionType;
import github.com.io.im2back.workflow_service.repositories.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public OutboxEvent savePending(UUID eventId, WorkflowActionType actionType, Long aggregateId, String payload) {
        return outboxEventRepository.save(new OutboxEvent(eventId, actionType, aggregateId, payload));
    }

    @Transactional
    public List<OutboxEvent> findPending() {
        return outboxEventRepository.findPendingForUpdate();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void markAsPublished(List<OutboxEvent> events) {
        events.forEach(OutboxEvent::markAsPublished);
        outboxEventRepository.saveAll(events);
    }
}