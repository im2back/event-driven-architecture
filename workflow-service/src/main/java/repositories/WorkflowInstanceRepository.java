package repositories;


import github.com.io.im2back.workflow_service.entities.instance.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, UUID> {

    Optional<WorkflowInstance> findByOrderId(Long orderId);
}