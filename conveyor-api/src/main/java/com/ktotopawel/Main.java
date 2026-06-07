package com.ktotopawel;

import com.ktotopawel.config.AppDbConfig;
import com.ktotopawel.config.AppObjectMapperConfig;
import com.ktotopawel.controller.JobController;
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
        Jdbi jdbi = AppDbConfig.createJdbi(mapper);
        logger.info("Database connection initialized successfully.");

        logger.info("Starting HTTP server...");

        JobService jobService = new JobService();
        JobController jobController = new JobController(jobService, mapper);
        JobRoutes jobRoutes = new JobRoutes(jobController);

        Javalin.create(
                        cfg -> {
                            cfg.routes.apiBuilder(jobRoutes);
                        }
                )
                .start(8000);
    }
}
