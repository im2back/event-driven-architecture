package github.com.io.im2back.order_service.controller.mapper;

import github.com.io.im2back.order_service.controller.dto.in.CreateOrderRequest;
import github.com.io.im2back.order_service.controller.dto.out.OrderResponse;
import github.com.io.im2back.order_service.entities.order.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static Order toEntity(CreateOrderRequest request) {
        return new Order(
                request.orderNumber(),
                request.tableNumber(),
                null,
                request.totalAmount(),
                request.currency()
        );
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTableNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}