package github.com.io.im2back.order_service.event.model;

import github.com.io.im2back.order_service.entities.order.Order;

public record OrderApplicationEvent(
        Order order,
        OrderEventType eventType
) {
}