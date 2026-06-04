WITH next_job AS (SELECT id
                  FROM jobs
                  WHERE state = 'available'

                     OR (state = 'claimed' AND lease_expires_at < now())

                  ORDER BY created_at
                  LIMIT 1 FOR UPDATE SKIP LOCKED),
     updated_job AS (
         UPDATE jobs
             SET
                 state = 'claimed',
                 owned_by_id = :workerId,
                 lease_expires_at = now() + (INTERVAL '1 second' * :leaseDurationSeconds),
                 started_at = now(),
                 retry_count = retry_count + 1
             FROM next_job
             WHERE jobs.id = next_job.id
             RETURNING jobs.*)
SELECT *
FROM updated_job;
