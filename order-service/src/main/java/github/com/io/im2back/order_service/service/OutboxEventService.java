package github.com.io.im2back.order_service.service;

import github.com.io.im2back.order_service.amqp.factory.model.OrderEventPayload;
import github.com.io.im2back.order_service.entities.outbox.OutboxEvent;
import github.com.io.im2back.order_service.entities.outbox.OutboxStatus;
import github.com.io.im2back.order_service.event.model.OrderEventType;

import github.com.io.im2back.order_service.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static github.com.io.im2back.order_service.entities.order.OrderStatus.CREATED;
import static github.com.io.im2back.order_service.event.model.OrderEventType.STATUS_UPDATED;

@Service
public class OutboxEventService {
    private final ObjectMapper objectMapper;

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public OutboxEvent savePending(OrderEventPayload event) {

        String payloadEvent = getStringPayload(event);

        OutboxEvent outboxEvent = new OutboxEvent(event.eventId(), event.eventType(), event.orderId(),
               payloadEvent, OutboxStatus.PENDING);

        return outboxEventRepository.save(outboxEvent);
    }

    private String getStringPayload(OrderEventPayload event) {
        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new RuntimeException("Error serializing outbox event", e);
        }
        return payload;
    }

    @Transactional
    public List<OutboxEvent> findPending() {
        return outboxEventRepository.findPendingForUpdate();
    }

    public void markAsPublished(List<OutboxEvent> events) {
        List<UUID> ids = events.stream().map(OutboxEvent::getId).toList();
        outboxEventRepository.markAsPublished(ids, LocalDateTime.now());
    }
}