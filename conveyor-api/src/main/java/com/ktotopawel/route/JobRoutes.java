package com.ktotopawel.route;

import com.ktotopawel.controller.JobController;
import io.javalin.apibuilder.EndpointGroup;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;

@RequiredArgsConstructor
public class JobRoutes implements EndpointGroup {

    private final JobController controller;

    @Override
    public void addEndpoints() {
        path("/jobs", () -> {
            before(ctx -> {
                UUID generatedJobId = UUID.randomUUID();
                MDC.put("jobId", generatedJobId.toString());
                ctx.attribute("jobId", generatedJobId);
            });
            post(controller::submitJob);
            after(ctx -> {
                MDC.clear();
            });
        });
    }
}
