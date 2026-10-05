package github.com.io.im2back.workflow_service.entities.transitionaction;

import github.com.io.im2back.workflow_service.entities.transition.WorkflowTransition;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "workflow_transition_action")
public class WorkflowTransitionAction {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transition_id", nullable = false)
    private WorkflowTransition transition;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private WorkflowActionType actionType;

    @Column(name = "action_value")
    private String actionValue;

    protected WorkflowTransitionAction() {
    }

    public WorkflowTransitionAction(WorkflowTransition transition, WorkflowActionType actionType, String actionValue) {
        this.id = UUID.randomUUID();
        this.transition = transition;
        this.actionType = actionType;
        this.actionValue = actionValue;
    }

    public UUID getId() {
        return id;
    }

    public WorkflowTransition getTransition() {
        return transition;
    }

    public WorkflowActionType getActionType() {
        return actionType;
    }

    public String getActionValue() {
        return actionValue;
    }
}