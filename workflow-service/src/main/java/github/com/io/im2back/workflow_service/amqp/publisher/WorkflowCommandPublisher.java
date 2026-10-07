package github.com.io.im2back.workflow_service.amqp.publisher;

import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowActionType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class WorkflowCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.commands}")
    private String exchange;

    @Value("${messaging.routing-key.update-order-status}")
    private String updateOrderStatusRoutingKey;

    @Value("${messaging.routing-key.process-payment}")
    private String processPaymentRoutingKey;

    @Value("${messaging.routing-key.prepare-order}")
    private String prepareOrderRoutingKey;

    @Value("${messaging.routing-key.reject-order}")
    private String rejectOrderRoutingKey;

    @Value("${messaging.routing-key.complete-order}")
    private String completeOrderRoutingKey;

    public WorkflowCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String payload, WorkflowActionType actionType) {
        String routingKey = switch (actionType) {
            case UPDATE_ORDER_STATUS -> updateOrderStatusRoutingKey;
            case PROCESS_PAYMENT -> processPaymentRoutingKey;
            case PREPARE_ORDER -> prepareOrderRoutingKey;
            case REJECT_ORDER -> rejectOrderRoutingKey;
            case COMPLETE_ORDER -> completeOrderRoutingKey;
        };

        Message message = MessageBuilder.withBody(payload.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();

        rabbitTemplate.send(exchange, routingKey, message);
    }
}