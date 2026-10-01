package github.com.io.im2back.order_service.service;


import github.com.io.im2back.order_service.entity.Order;
import github.com.io.im2back.order_service.entity.OrderStatus;
import github.com.io.im2back.order_service.repository.OrderRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Cacheable(
            value = "orders",
            key = "#idempotencyKey"
    )
    public Order create(String idempotencyKey, Order order) {

        order.setStatus(OrderStatus.CREATED);

        return orderRepository.save(order);
    }
}