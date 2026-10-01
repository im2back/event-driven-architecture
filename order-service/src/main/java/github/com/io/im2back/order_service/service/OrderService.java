package github.com.io.im2back.order_service.service;


import github.com.io.im2back.order_service.entity.Order;
import github.com.io.im2back.order_service.entity.OrderStatus;
import github.com.io.im2back.order_service.repository.OrderRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Cacheable(
            value = "order-idempotency",
            key = "#idempotencyKey"
    )
    public Order create(
            String idempotencyKey,
            Order order
    ) {
        order.setStatus(OrderStatus.CREATED);

        return orderRepository.save(order);
    }

    @Cacheable(
            value = "orders",
            key = "#id"
    )
    public Order findById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );
    }

    @CachePut(
            value = "orders",
            key = "#id"
    )
    public Order update(
            Long id,
            Order order
    ) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );

        existingOrder.setOrderNumber(order.getOrderNumber());
        existingOrder.setTableNumber(order.getTableNumber());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setCurrency(order.getCurrency());

        return orderRepository.save(existingOrder);
    }

    @CacheEvict(
            value = "orders",
            key = "#id"
    )
    public void delete(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );

        orderRepository.delete(order);
    }
}