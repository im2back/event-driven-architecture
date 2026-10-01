package github.com.io.im2back.order_service.scheduler;


import github.com.io.im2back.order_service.amqp.publisher.OrderEventPublisher;
import github.com.io.im2back.order_service.entities.outbox.OutboxEvent;
import github.com.io.im2back.order_service.event.model.OrderEventType;
import github.com.io.im2back.order_service.service.OutboxEventService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxScheduler {

    private final OutboxEventService outboxEventService;
    private final OrderEventPublisher orderEventPublisher;

    public OutboxScheduler(OutboxEventService outboxEventService, OrderEventPublisher orderEventPublisher) {
        this.outboxEventService = outboxEventService;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay}")
    public void processPendingEvents() {
        List<OutboxEvent> events = outboxEventService.findPending();

        for (OutboxEvent event : events) {
            orderEventPublisher.publish(event.getPayload(), OrderEventType.valueOf(event.getEventType()));
        }
    }
}