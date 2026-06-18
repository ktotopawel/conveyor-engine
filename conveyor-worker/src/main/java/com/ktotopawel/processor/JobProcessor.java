package com.ktotopawel.processor;

import com.ktotopawel.model.Job;

@FunctionalInterface
public interface JobProcessor {
    abstract void process(Job job);
}
