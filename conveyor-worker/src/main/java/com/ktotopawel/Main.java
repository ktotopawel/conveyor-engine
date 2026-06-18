package com.ktotopawel;

import com.ktotopawel.config.AppObjectMapperConfig;
import com.ktotopawel.config.BackoffConfig;
import com.ktotopawel.config.WorkerConfig;
import com.ktotopawel.config.db.AppDbConfig;
import com.ktotopawel.dispatcher.JobDispatcher;
import com.ktotopawel.model.Job;
import com.ktotopawel.processor.MdcJobProcessorDecorator;
import com.ktotopawel.processor.TestProcessor;
import com.ktotopawel.signaler.Signaler;
import com.ktotopawel.processor.JobProcessor;
import com.ktotopawel.worker.Worker;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static void main(String[] args) {
        Logger mainLogger = LoggerFactory.getLogger(Main.class);

        mainLogger.info("Starting Worker App");
        mainLogger.info("Creating db connections...");

        ObjectMapper mapper = AppObjectMapperConfig.createObjectMapper();

        Jdbi jdbi = AppDbConfig.configure()
                .mapper(mapper)
                .maxPoolSize(10)
                .minIdle(2)
                .create();

        mainLogger.info("Created jdbi connection.");

        HikariDataSource listenerDataSource = AppDbConfig.createListenerDataSource();

        mainLogger.info("Created listener data source.");


        Signaler signaler = new Signaler(listenerDataSource);

        WorkerConfig config = WorkerConfig.builder()
                .leaseDuration(30000)
                .backoffConfig(new BackoffConfig())
                .build();

        JobDispatcher dispatcher = new JobDispatcher();
        dispatcher.registerJobProcessor("test", new TestProcessor());

        Worker mainWorker = new Worker(jdbi, config, dispatcher);

        signaler.subscribe(mainWorker);

        ExecutorService appExecutor = Executors.newFixedThreadPool(2);

        mainLogger.info("Starting signaler...");
        appExecutor.execute(signaler);

        mainLogger.info("Starting worker(s)...");
        appExecutor.execute(mainWorker);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            mainLogger.info("Shutting down application...");
            appExecutor.shutdown();
        }));
    }
}
