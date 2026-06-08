package com.ktotopawel.service;

import com.ktotopawel.dto.SubmitJobDto;
import com.ktotopawel.repository.JobRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;

    public void submitJob(SubmitJobDto dto) {
        repository.save(dto);
    }
}
