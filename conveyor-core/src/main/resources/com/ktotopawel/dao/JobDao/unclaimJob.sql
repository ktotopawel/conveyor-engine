UPDATE jobs
SET state            = CASE
                           WHEN retry_count > 3 THEN 'failed'
                           ELSE 'pending'
    END,
    owned_by_id      = CASE
                           WHEN retry_count > 3 THEN owned_by_id
                           ELSE NULL
        END, -- if retry count is above 3, we keep the worker id to be able to query for failed jobs by worker, otherwise we set it to null to make the job available for other workers
    lease_expires_at = CASE
                           WHEN retry_count > 3 THEN lease_expires_at
                           ELSE NULL
        END,
    last_error       = :errorMessage
WHERE id = :jobId
  AND state = 'claimed'
  AND owned_by_id = :workerId;