package repositories;

import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowState;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowTransition;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkflowTransitionActionRepository extends JpaRepository<WorkflowTransitionAction, UUID> {

    List<WorkflowTransitionAction> findByTransition(WorkflowTransition transition);
}