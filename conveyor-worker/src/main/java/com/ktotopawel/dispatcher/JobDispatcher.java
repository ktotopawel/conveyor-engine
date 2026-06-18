package com.ktotopawel.dispatcher;

import com.ktotopawel.model.Job;
import com.ktotopawel.processor.JobProcessor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class JobDispatcher {

    private final HashMap<String, JobProcessor> registry = new HashMap<>();

    public JobProcessor getProcessor(Job job) {
        return registry.get(job.name());
    }

    public void registerJobProcessor(String jobName, JobProcessor processor) {
        registry.put(jobName, processor);
    }
}
