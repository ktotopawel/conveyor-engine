package com.ktotopawel.service;

import com.ktotopawel.model.Job;
import com.ktotopawel.repository.JobRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;

    public void submitJob(Job job) {
        repository.save(job);
    }
}
