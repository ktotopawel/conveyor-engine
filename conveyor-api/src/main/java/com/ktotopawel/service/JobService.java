package com.ktotopawel.service;

import com.ktotopawel.dto.SubmitJobDto;
import com.ktotopawel.repository.JobRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;

    public void submitJob(UUID id, SubmitJobDto dto) {
        repository.save(id, dto);
    }
}
