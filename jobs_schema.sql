CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(32) NOT NULL,
    owned_by_id VARCHAR(128), -- ID OF THE WORKER OWNING THE JOB
    lease_expires_at TIMESTAMPTZ,
    retry_count INTEGER DEFAULT 0,
    state VARCHAR(16) DEFAULT 'pending',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    job_data JSONB,
    last_error TEXT
);
