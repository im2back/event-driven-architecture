package github.com.io.im2back.order_service.event.listener;

import github.com.io.im2back.order_service.entity.Order;
import github.com.io.im2back.order_service.event.model.OrderApplicationEvent;
import github.com.io.im2back.order_service.event.model.OrderEventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventNotifier {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventNotifier(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notify(Order order, OrderEventType eventType) {
        eventPublisher.publishEvent(new OrderApplicationEvent(order, eventType));
    }
}