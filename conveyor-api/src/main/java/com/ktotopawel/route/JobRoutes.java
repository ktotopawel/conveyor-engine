package com.ktotopawel.route;

import com.ktotopawel.controller.JobController;
import io.javalin.apibuilder.EndpointGroup;
import lombok.RequiredArgsConstructor;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

@RequiredArgsConstructor
public class JobRoutes implements EndpointGroup {

    private final JobController controller;

    @Override
    public void addEndpoints() {
        path("/jobs", () -> {
            post(controller::submitJob);
        });
    }
}
