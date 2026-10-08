package github.com.io.im2back.order_service.amqp.dto.output;

import github.com.io.im2back.order_service.entities.order.OrderStatus;

import java.math.BigDecimal;

public record OrderCreatedEventsData(
        Integer tableNumber,
        BigDecimal totalAmount,
        String currency,
        OrderStatus status
) {
}