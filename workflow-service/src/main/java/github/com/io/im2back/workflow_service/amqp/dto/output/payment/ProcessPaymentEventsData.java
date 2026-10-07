package github.com.io.im2back.workflow_service.amqp.dto.output.payment;

import java.math.BigDecimal;

public record ProcessPaymentEventsData(
        BigDecimal totalAmount,
        String currency
) {
}