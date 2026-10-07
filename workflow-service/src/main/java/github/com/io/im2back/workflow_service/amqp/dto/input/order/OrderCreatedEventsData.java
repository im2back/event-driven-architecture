package github.com.io.im2back.workflow_service.amqp.dto.input.order;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;

import java.math.BigDecimal;

public record OrderCreatedEventsData(
        Integer tableNumber,
        BigDecimal totalAmount,
        String currency,
        String status
) implements WorkflowEventsData {
}