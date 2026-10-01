package github.com.io.im2back.order_service.controller;

import github.com.io.im2back.order_service.controller.dto.in.CreateOrderRequest;
import github.com.io.im2back.order_service.controller.dto.out.OrderResponse;
import github.com.io.im2back.order_service.controller.mapper.OrderMapper;
import github.com.io.im2back.order_service.entity.Order;
import github.com.io.im2back.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request ) {

        Order order = OrderMapper.toEntity(request);

        Order createdOrder = orderService.create(
                idempotencyKey,
                order
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrderMapper.toResponse(createdOrder));
    }
}