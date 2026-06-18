package com.ktotopawel.processor;

import com.ktotopawel.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestProcessor implements JobProcessor {

    Logger logger = LoggerFactory.getLogger(TestProcessor.class);

    @Override
    public void process(Job job) {
        logger.info("Processed job: {} with data: {}", job.id(), job.jobData());
    }
}
