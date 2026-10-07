package github.com.io.im2back.workflow_service.service;

import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventsData;
import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import github.com.io.im2back.workflow_service.entities.instance.WorkflowInstance;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowState;
import github.com.io.im2back.workflow_service.entities.transition.WorkflowTransition;
import github.com.io.im2back.workflow_service.entities.transitionaction.WorkflowTransitionAction;
import github.com.io.im2back.workflow_service.repositories.WorkflowInstanceRepository;
import github.com.io.im2back.workflow_service.repositories.WorkflowTransitionActionRepository;
import github.com.io.im2back.workflow_service.repositories.WorkflowTransitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkflowService {

    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowTransitionActionRepository workflowTransitionActionRepository;

    public WorkflowService(
            WorkflowInstanceRepository workflowInstanceRepository,
            WorkflowTransitionRepository workflowTransitionRepository,
            WorkflowTransitionActionRepository workflowTransitionActionRepository) {
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.workflowTransitionActionRepository = workflowTransitionActionRepository;
    }

    @Transactional
    public List<WorkflowTransitionAction> process(
            WorkflowEventPayload<? extends WorkflowEventsData> event) {

        WorkflowEventType eventType = WorkflowEventType.valueOf(event.eventType());

        WorkflowInstance instance = workflowInstanceRepository.findByOrderId(event.orderId())
                .orElseGet(() -> createInitialInstance(event.orderId(), eventType));

        WorkflowTransition transition = workflowTransitionRepository
                .findByCurrentStateAndEventType(instance.getCurrentState(), eventType)
                .orElseThrow(() -> new IllegalStateException(
                        "Transition not found for state " + instance.getCurrentState() + " and event " + eventType));

        instance.changeState(transition.getNextState());
        workflowInstanceRepository.save(instance);

        return workflowTransitionActionRepository.findByTransition(transition);
    }

    private WorkflowInstance createInitialInstance(Long orderId, WorkflowEventType eventType) {
        if (eventType != WorkflowEventType.ORDER_CREATED) {
            throw new IllegalStateException("Workflow instance not found for order " + orderId);
        }

        return workflowInstanceRepository.save(new WorkflowInstance(orderId, WorkflowState.INITIAL));
    }
}