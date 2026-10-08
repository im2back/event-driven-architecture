package github.com.io.im2back.order_service.amqp.listener;

import github.com.io.im2back.order_service.amqp.dto.input.OrderCommandPayload;
import github.com.io.im2back.order_service.amqp.dto.input.UpdateOrderStatusEventsData;
import github.com.io.im2back.order_service.amqp.factory.OrderCommandDataFactory;
import github.com.io.im2back.order_service.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCommandListener {

    private final OrderCommandDataFactory orderCommandDataFactory;
    private final OrderService orderService;
    private final MetadataStoreSelector metadataStoreSelector;

    public OrderCommandListener(OrderCommandDataFactory orderCommandDataFactory, OrderService orderService, MetadataStoreSelector metadataStoreSelector) {
        this.orderCommandDataFactory = orderCommandDataFactory;
        this.orderService = orderService;
        this.metadataStoreSelector = metadataStoreSelector;
    }

    @Transactional
    @RabbitListener(queues = "${messaging.queue.order-commands}")
    public void consume(OrderCommandPayload<?> command) {
        Message<?> message = MessageBuilder.withPayload(command).build();

        if (!metadataStoreSelector.accept(message)) {
            return;
        }

        OrderCommandPayload<?> typedCommand = orderCommandDataFactory.create(command);

        if (typedCommand.eventsData() instanceof UpdateOrderStatusEventsData data) {
            orderService.updateStatus(typedCommand.orderId(), data.status());
        }
    }
}