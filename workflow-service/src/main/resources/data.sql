INSERT INTO workflow_transition (
    id,
    current_state,
    event_type,
    next_state
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    'INITIAL',
    'ORDER_CREATED',
    'WAITING_PAYMENT'
);

INSERT INTO workflow_transition_action (
    id,
    transition_id,
    action_type,
    action_value
) VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'PROCESS_PAYMENT',
    NULL
);