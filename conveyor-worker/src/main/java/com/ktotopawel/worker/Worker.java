package com.ktotopawel.worker;

import com.ktotopawel.config.BackoffConfig;
import com.ktotopawel.config.WorkerConfig;
import com.ktotopawel.dao.JobDao;
import com.ktotopawel.dispatcher.JobDispatcher;
import com.ktotopawel.model.Job;
import com.ktotopawel.processor.JobProcessor;
import com.ktotopawel.processor.MdcJobProcessorDecorator;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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
    private final JobDispatcher dispatcher;

    private final Lock lock = new ReentrantLock();
    private final Condition newJobCondition = lock.newCondition();

    @Override
    public void run() {
        logger.info("Worker {} started, polling for jobs...", workerId);

        int tries = 0;
        BackoffConfig backoffConfig = config.getBackoffConfig();

        try (MDC.MDCCloseable closeable = MDC.putCloseable("workerId", String.valueOf(workerId))) {
            while (true) {
                try {
                    Optional<Job> jobToProcess = claimJob();

                    if (jobToProcess.isPresent()) {
                        try {
                            handleJob(jobToProcess.get());
                        } catch (Exception e) {
                            logger.warn("Job thrown exception", e);
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
                    } catch (InterruptedException e) {
                        logger.warn("Thread {} interrupted", Thread.currentThread().getName());
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        lock.unlock();
                    }
                } catch (Exception e) {
                    logger.error("Worker thread encountered a fatal error during polling!", e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private Optional<Job> claimJob() {
        return jdbi.withExtension(JobDao.class,
                dao -> dao.claimJob(this.workerId.toString(), config.getLeaseDuration())
        );
    }

    private void handleJob(Job job) {
        JobProcessor processor = dispatcher.getProcessor(job);
        JobProcessor decoratedJobProcessor = new MdcJobProcessorDecorator(processor);
        decoratedJobProcessor.process(job);
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
