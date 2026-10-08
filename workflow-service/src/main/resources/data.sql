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

INSERT INTO workflow_transition_action (
    id,
    transition_id,
    action_type,
    action_value
) VALUES (
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'UPDATE_ORDER_STATUS',
    'WAITING_PAYMENT'
);

CREATE TABLE INT_METADATA_STORE (
    METADATA_KEY VARCHAR(255) NOT NULL,
    METADATA_VALUE VARCHAR(4000) NOT NULL,
    REGION VARCHAR(100) NOT NULL,
    CONSTRAINT INT_METADATA_STORE_PK PRIMARY KEY (METADATA_KEY, REGION)
);