package github.com.io.im2back.order_service.amqp.listener;


public record UpdateOrderStatusEventsData(
        String status
) {
}