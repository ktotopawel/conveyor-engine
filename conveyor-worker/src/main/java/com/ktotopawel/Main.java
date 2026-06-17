package com.ktotopawel;

import com.ktotopawel.config.BackoffConfig;
import com.ktotopawel.config.WorkerConfig;
import com.ktotopawel.config.db.AppDbConfig;
import com.ktotopawel.model.Job;
import com.ktotopawel.signaler.Signaler;
import com.ktotopawel.worker.JobProcessor;
import com.ktotopawel.worker.Worker;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static void main(String[] args) {
        Logger mainLogger = LoggerFactory.getLogger(Main.class);

        mainLogger.info("Starting Worker App");
        mainLogger.info("Creating db connections...");

        Jdbi jdbi = AppDbConfig.configure().maxPoolSize(10).minIdle(2).create();

        mainLogger.info("Created jdbi connection.");

        HikariDataSource listenerDataSource = AppDbConfig.createListenerDataSource();

        mainLogger.info("Created listener data source.");


        Signaler signaler = new Signaler(listenerDataSource);

        WorkerConfig config = WorkerConfig.builder()
                .leaseDuration(30000)
                .backoffConfig(new BackoffConfig())
                .build();

        JobProcessor testProcessor = new JobProcessor() {

            private final Logger logger = LoggerFactory.getLogger("TestProcessor");

            @Override
            public void process(Job job) throws Exception {
                logger.info("Processing job: {}, with ID: {}", job.name(), job.id());
            }
        };

        Worker mainWorker = new Worker(jdbi, config, testProcessor);

        signaler.subscribe(mainWorker);

        mainLogger.info("Starting signaler...");

        signaler.run();

        mainLogger.info("Starting worker(s)...");

        mainWorker.run();
    }
}
