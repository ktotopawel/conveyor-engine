package com.ktotopawel;

import com.ktotopawel.config.AppObjectMapperConfig;
import com.ktotopawel.config.db.AppDbConfig;
import com.ktotopawel.controller.JobController;
import com.ktotopawel.dao.JobDao;
import com.ktotopawel.repository.JobRepository;
import com.ktotopawel.route.JobRoutes;
import com.ktotopawel.service.JobService;
import io.javalin.Javalin;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger("Main");

        logger.info("Starting application...");

        ObjectMapper mapper = AppObjectMapperConfig.createObjectMapper();

        logger.info("Initializing database connection...");

        Jdbi jdbi = AppDbConfig.configure()
                .maxPoolSize(10)
                .minIdle(2)
                .mapper(mapper)
                .create();

        logger.info("Database connection initialized successfully.");

        logger.info("Creating database schema...");
        jdbi.useHandle(handle -> {
            JobDao dao = handle.attach(JobDao.class);
            dao.createTable();
        });
        logger.info("Database schema created successfully.");

        logger.info("Starting HTTP server...");

        JobRepository jobRepository = new JobRepository(jdbi);
        JobService jobService = new JobService(jobRepository);
        JobController jobController = new JobController(jobService, mapper);
        JobRoutes jobRoutes = new JobRoutes(jobController);

        Javalin.create(
                        cfg -> {
                            cfg.requestLogger.http((ctx, ms) -> {
                                logger.info("{} {} - Status: {} (took {}ms)",
                                        ctx.method(),
                                        ctx.path(),
                                        ctx.status(),
                                        ms);
                            });
                            cfg.routes.apiBuilder(jobRoutes);
                        }
                )
                .start(8000);
    }
}
