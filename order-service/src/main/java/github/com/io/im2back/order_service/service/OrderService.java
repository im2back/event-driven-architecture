package github.com.io.im2back.order_service.service;


import github.com.io.im2back.order_service.entities.order.Order;
import github.com.io.im2back.order_service.entities.order.OrderStatus;
import github.com.io.im2back.order_service.event.listener.OrderEventNotifier;
import github.com.io.im2back.order_service.event.model.OrderEventType;
import github.com.io.im2back.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;


@Service
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final OrderEventNotifier orderEventNotifier;

    public OrderService(OrderRepository orderRepository,
                        ApplicationEventPublisher eventPublisher,OrderEventNotifier orderEventNotifier) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.orderEventNotifier = orderEventNotifier;
    }

    @Cacheable(value = "order-idempotency", key = "#idempotencyKey")
    @Transactional
    public Order create(String idempotencyKey, Order order) {

        order.setStatus(OrderStatus.CREATED);
        Order savedOrder = orderRepository.save(order);

        orderEventNotifier.notify(savedOrder, OrderEventType.ORDER_CREATED);

        return savedOrder;
    }

    @Cacheable(value = "orders", key = "#id")
    public Order findById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );
    }

    @CachePut(value = "orders", key = "#id")
    @Transactional
    public Order update(Long id, Order order) {

        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found: " + id));

        existingOrder.setOrderNumber(order.getOrderNumber());
        existingOrder.setTableNumber(order.getTableNumber());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setCurrency(order.getCurrency());
        existingOrder.setStatus(order.getStatus());

        Order savedOrder = orderRepository.save(existingOrder);

        orderEventNotifier.notify(savedOrder, OrderEventType.STATUS_UPDATED);

        return savedOrder;
    }

    @CacheEvict(value = "orders", key = "#id")
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );

        orderRepository.delete(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, String status, Long version) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (version <= order.getWorkflowVersion()) {
            return order;
        }

        order.setStatus(OrderStatus.valueOf(status));
        order.setWorkflowVersion(version);

        return orderRepository.save(order);
    }
}