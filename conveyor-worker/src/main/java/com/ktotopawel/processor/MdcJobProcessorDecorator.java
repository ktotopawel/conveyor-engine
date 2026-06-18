package com.ktotopawel.processor;

import com.ktotopawel.model.Job;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

@RequiredArgsConstructor
public class MdcJobProcessorDecorator implements JobProcessor {

    private final JobProcessor delegate;

    @Override
    public void process(Job job) {
        try (MDC.MDCCloseable closeable = MDC.putCloseable("jobId", String.valueOf(job.id()))){
            delegate.process(job);
        }
    }
}
