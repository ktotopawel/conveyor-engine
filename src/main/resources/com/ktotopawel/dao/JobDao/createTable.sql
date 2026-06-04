CREATE TABLE IF NOT EXISTS jobs
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    name             VARCHAR(32) NOT NULL,
    owned_by_id      VARCHAR(128),
    lease_expires_at TIMESTAMPTZ,
    retry_count      INTEGER              DEFAULT 0,
    state            VARCHAR(16)          DEFAULT 'pending', -- 'pending' OR 'claimed' OR 'completed' OR 'failed',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    started_at       TIMESTAMPTZ,
    completed_at     TIMESTAMPTZ,
    job_data         JSONB,
    last_error       TEXT                                    -- stored in the db AS STATE (enables querying DQ for specific fail reason)
);

CREATE INDEX idx_jobs_processing_queue
    ON jobs (created_at ASC)
    INCLUDE (lease_expires_at)
    WHERE state IN ('available', 'claimed');

