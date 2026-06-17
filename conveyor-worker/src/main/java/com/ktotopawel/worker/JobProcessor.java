package com.ktotopawel.worker;

import com.ktotopawel.model.Job;

@FunctionalInterface
public interface JobProcessor {
    abstract void process(Job job) throws Exception;
}
