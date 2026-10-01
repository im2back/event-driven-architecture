package github.com.io.im2back.order_service.event.listener;

import github.com.io.im2back.order_service.amqp.factory.OrderEventFactory;
import github.com.io.im2back.order_service.amqp.publisher.OrderEventPublisher;

import github.com.io.im2back.order_service.event.model.OrderApplicationEvent;
import github.com.io.im2back.order_service.service.OutboxEventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    private final OrderEventPublisher orderEventPublisher;
    private final OrderEventFactory orderEventFactory;
    private final OutboxEventService outboxEventService;

    public OrderEventListener(
            OrderEventPublisher orderEventPublisher,
            OrderEventFactory orderEventFactory,
            OutboxEventService outboxEventService
    ) {
        this.orderEventPublisher = orderEventPublisher;
        this.orderEventFactory = orderEventFactory;
        this.outboxEventService = outboxEventService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderApplicationEvent event) {

        var payload = orderEventFactory.from(event.order(), event.eventType());

        outboxEventService.savePending(payload);
    }
}