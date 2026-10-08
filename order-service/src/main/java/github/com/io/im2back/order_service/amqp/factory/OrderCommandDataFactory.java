package github.com.io.im2back.order_service.amqp.factory;

import github.com.io.im2back.order_service.amqp.dto.input.OrderCommandPayload;
import github.com.io.im2back.order_service.amqp.dto.input.UpdateOrderStatusEventsData;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderCommandDataFactory {

    private final ObjectMapper objectMapper;

    public OrderCommandDataFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderCommandPayload<?> create(OrderCommandPayload<?> command) {
        return switch (command.eventType()) {
            case "UPDATE_ORDER_STATUS" -> new OrderCommandPayload<>(
                    command.eventId(),
                    command.eventType(),
                    command.orderId(),
                    command.orderNumber(),
                    command.createdAt(),
                    objectMapper.convertValue(command.eventsData(), UpdateOrderStatusEventsData.class)
            );

            default -> throw new IllegalArgumentException("Unsupported command type: " + command.eventType());
        };
    }
}