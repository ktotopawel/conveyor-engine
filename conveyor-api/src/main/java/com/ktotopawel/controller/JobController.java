package com.ktotopawel.controller;

import com.ktotopawel.model.Job;
import com.ktotopawel.service.JobService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;


@RequiredArgsConstructor
public class JobController {

    private final JobService service;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(JobController.class);

    public void submitJob(Context ctx) {
        logger.debug("Received body: {}", ctx.body());
        Job job = mapper.readValue(ctx.body(), Job.class);
        logger.debug("Parsed job: {}", job);
        UUID jobId = ctx.attribute("jobId");
        job = job.withId(jobId);
        service.submitJob(job);
        ctx.status(HttpStatus.ACCEPTED).json("Job submitted successfully");
    }
}