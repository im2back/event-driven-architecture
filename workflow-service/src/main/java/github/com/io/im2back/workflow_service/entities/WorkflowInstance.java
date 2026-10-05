package github.com.io.im2back.workflow_service.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_instance")
public class WorkflowInstance {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private WorkflowState currentState;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected WorkflowInstance() {
    }

    public WorkflowInstance(Long orderId, WorkflowState currentState) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.currentState = currentState;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public WorkflowState getCurrentState() {
        return currentState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeState(WorkflowState newState) {
        this.currentState = newState;
        this.updatedAt = LocalDateTime.now();
    }
}