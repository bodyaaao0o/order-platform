CREATE TABLE processed_events
(
    id BIGSERIAL PRIMARY KEY,

    event_id VARCHAR(255) UNIQUE NOT NULL,

    processed_at TIMESTAMP NOT NULL
);