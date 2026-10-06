package github.com.io.im2back.workflow_service.repositories;

import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowState;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, UUID> {

    Optional<WorkflowTransition> findByCurrentStateAndEventType(WorkflowState currentState, WorkflowEventType eventType);
}