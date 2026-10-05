package github.com.io.im2back.workflow_service.entities.transition;

import github.com.io.im2back.workflow_service.entities.instance.WorkflowEventType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "workflow_transition")
public class WorkflowTransition {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private WorkflowState currentState;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private WorkflowEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_state", nullable = false)
    private WorkflowState nextState;

    protected WorkflowTransition() {
    }

    public WorkflowTransition(WorkflowState currentState, WorkflowEventType eventType, WorkflowState nextState) {
        this.id = UUID.randomUUID();
        this.currentState = currentState;
        this.eventType = eventType;
        this.nextState = nextState;
    }

    public UUID getId() {
        return id;
    }

    public WorkflowState getCurrentState() {
        return currentState;
    }

    public WorkflowEventType getEventType() {
        return eventType;
    }

    public WorkflowState getNextState() {
        return nextState;
    }
}