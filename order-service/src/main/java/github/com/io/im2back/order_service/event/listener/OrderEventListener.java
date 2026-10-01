package github.com.io.im2back.order_service.event.listener;

import github.com.io.im2back.order_service.amqp.factory.OrderEventFactory;
import github.com.io.im2back.order_service.amqp.publisher.OrderEventPublisher;

import github.com.io.im2back.order_service.event.model.OrderApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    private final OrderEventPublisher orderEventPublisher;
    private final OrderEventFactory orderEventFactory;

    public OrderEventListener(
            OrderEventPublisher orderEventPublisher,
            OrderEventFactory orderEventFactory
    ) {
        this.orderEventPublisher = orderEventPublisher;
        this.orderEventFactory = orderEventFactory;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderApplicationEvent event) {

        orderEventPublisher.publish(orderEventFactory.from(event.order(), event.eventType()),
                event.eventType()
        );
    }
}