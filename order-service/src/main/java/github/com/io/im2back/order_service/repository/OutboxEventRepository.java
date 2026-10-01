package github.com.io.im2back.order_service.repository;
import github.com.io.im2back.order_service.entities.outbox.OutboxEvent;
import github.com.io.im2back.order_service.entities.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}