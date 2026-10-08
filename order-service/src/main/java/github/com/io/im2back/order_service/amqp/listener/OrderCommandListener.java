package github.com.io.im2back.order_service.amqp.listener;

import github.com.io.im2back.order_service.amqp.dto.input.OrderCommandPayload;
import github.com.io.im2back.order_service.amqp.dto.input.UpdateOrderStatusEventsData;
import github.com.io.im2back.order_service.amqp.factory.OrderCommandDataFactory;
import github.com.io.im2back.order_service.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandListener {

    private final OrderCommandDataFactory orderCommandDataFactory;
    private final OrderService orderService;

    public OrderCommandListener(OrderCommandDataFactory orderCommandDataFactory, OrderService orderService) {
        this.orderCommandDataFactory = orderCommandDataFactory;
        this.orderService = orderService;
    }

    @RabbitListener(queues = "${messaging.queue.order-commands}")
    public void consume(OrderCommandPayload<?> command) {
        OrderCommandPayload<?> typedCommand = orderCommandDataFactory.create(command);

        if (typedCommand.eventsData() instanceof UpdateOrderStatusEventsData data) {
            orderService.updateStatus(typedCommand.orderId(), data.status());
        }
    }
}