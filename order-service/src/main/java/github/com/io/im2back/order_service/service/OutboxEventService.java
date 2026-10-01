package github.com.io.im2back.order_service.service;

import github.com.io.im2back.order_service.entities.outbox.OutboxEvent;
import github.com.io.im2back.order_service.entities.outbox.OutboxStatus;
import github.com.io.im2back.order_service.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventService(
            OutboxEventRepository outboxEventRepository
    ) {
        this.outboxEventRepository = outboxEventRepository;
    }

    public OutboxEvent savePending(
            UUID eventId,
            String eventType,
            Long aggregateId,
            String routingKey,
            String payload
    ) {

        OutboxEvent outboxEvent = new OutboxEvent(
                eventId,
                eventType,
                aggregateId,
                routingKey,
                payload,
                OutboxStatus.PENDING
        );

        return outboxEventRepository.save(outboxEvent);
    }

    public List<OutboxEvent> findPending() {
        return outboxEventRepository
                .findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
    }

    @Transactional
    public void markAsPublished(OutboxEvent event) {

        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());

        outboxEventRepository.save(event);
    }
}