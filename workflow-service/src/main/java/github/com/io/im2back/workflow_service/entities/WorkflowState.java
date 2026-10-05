package github.com.io.im2back.workflow_service.entities;

public enum WorkflowState {
    INITIAL,
    WAITING_PAYMENT,
    WAITING_PREPARATION,
    COMPLETED,
    PAYMENT_REJECTED
}