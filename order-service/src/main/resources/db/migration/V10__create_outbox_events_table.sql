CREATE TABLE outbox_events (

    id BIGSERIAL PRIMARY KEY,

    event_type VARCHAR(255) NOT NULL,

    payload TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    processed BOOLEAN NOT NULL DEFAULT FALSE
);