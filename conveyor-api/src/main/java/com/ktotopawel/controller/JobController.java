package com.ktotopawel.controller;

import com.ktotopawel.dto.SubmitJobDto;
import com.ktotopawel.service.JobService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;


@RequiredArgsConstructor
public class JobController {

    private final JobService service;
    private final ObjectMapper mapper;

    public void submitJob(Context ctx) {
        SubmitJobDto dto = mapper.readValue(ctx.body(), SubmitJobDto.class);
        service.submitJob(dto);
        ctx.status(HttpStatus.ACCEPTED).json("Job submitted successfully");
    }
}