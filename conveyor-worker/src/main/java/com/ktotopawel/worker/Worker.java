package com.ktotopawel.worker;

import com.ktotopawel.config.BackoffConfig;
import com.ktotopawel.config.WorkerConfig;
import com.ktotopawel.dao.JobDao;
import com.ktotopawel.model.Job;
import com.sun.source.tree.TryTree;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class Worker implements Runnable {

    private final UUID workerId = UUID.randomUUID();

    private final Jdbi jdbi;
    private final Logger logger = LoggerFactory.getLogger(Worker.class);
    private final WorkerConfig config;
    private final JobProcessor processor;

    private final Lock lock = new ReentrantLock();
    private final Condition newJobCondition = lock.newCondition();

    @Override
    public void run() {
        int tries = 0;

        BackoffConfig backoffConfig = config.getBackoffConfig();

        while (true) {
            Optional<Job> jobToProcess = claimJob();

            if (jobToProcess.isPresent()) {
                try {
                    processor.process(jobToProcess.get());
                } catch (Exception e) {
                    logger.warn("Job thrown exception");
                    // todo: handle job exception throwing
                }
                tries = 0;
                continue;
            }

            lock.lock();
            try {
                long currentBackoff = Math.min(
                        backoffConfig.getMaxDelayMillis(),
                        (long) (backoffConfig.getInitialDelayMillis() * Math.pow(backoffConfig.getExponent(), tries))
                );

                boolean signaled = newJobCondition.await(currentBackoff, TimeUnit.MILLISECONDS);

                if (signaled) {
                    tries = 0;
                } else {
                    tries++;
                }
            } catch (InterruptedException _) {
                logger.warn("Thread {} interrupted", Thread.currentThread().getName());
            } finally {
                lock.unlock();
            }
        }
    }

    private Optional<Job> claimJob() {
        return jdbi.withExtension(JobDao.class,
                dao -> dao.claimJob(this.workerId.toString(), config.getLeaseDuration())
        );
    }

    public void externalSignal(PGNotification notification) {
        lock.lock();
        try {
            logger.info("Received notification: {}", notification.toString());
            newJobCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
