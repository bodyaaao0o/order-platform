CREATE TABLE saga_instances
(
    id              BIGSERIAL PRIMARY KEY,

    order_id        BIGINT      NOT NULL UNIQUE,

    status          VARCHAR(64) NOT NULL,

    current_step    VARCHAR(64) NOT NULL,

    failure_reason  VARCHAR(1000),

    retry_count     INTEGER     NOT NULL DEFAULT 0,

    max_retries     INTEGER     NOT NULL DEFAULT 3,

    last_event_type VARCHAR(255),

    last_event_at   TIMESTAMP   NOT NULL,

    created_at      TIMESTAMP   NOT NULL,

    updated_at      TIMESTAMP   NOT NULL,

    completed_at    TIMESTAMP
);

CREATE INDEX idx_saga_order_id
    ON saga_instances(order_id);

CREATE INDEX idx_saga_status
    ON saga_instances(status);

CREATE INDEX idx_saga_updated_at
    ON saga_instances(updated_at);
