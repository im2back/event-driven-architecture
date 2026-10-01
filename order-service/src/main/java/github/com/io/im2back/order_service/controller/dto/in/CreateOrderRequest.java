package github.com.io.im2back.order_service.controller.dto.in;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


public record CreateOrderRequest(

        @NotBlank(message = "Order number cannot be blank")
        String orderNumber,

        @NotNull(message = "Table number cannot be null")
        @Positive(message = "Table number must be greater than zero")
        Integer tableNumber,

        @NotNull(message = "Total amount cannot be null")
        @PositiveOrZero(message = "Total amount cannot be negative")
        BigDecimal totalAmount,

        @NotBlank(message = "Currency cannot be blank")
        @Size(
                min = 3,
                max = 3,
                message = "Currency must have exactly 3 characters"
        )
        String currency

) {
}