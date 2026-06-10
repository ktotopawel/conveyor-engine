package com.ktotopawel.repository;

import com.ktotopawel.dao.JobDao;
import com.ktotopawel.dto.SubmitJobDto;
import com.ktotopawel.model.Job;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import java.util.UUID;

@RequiredArgsConstructor
public class JobRepository {

    private final Jdbi jdbi;

    public void save(UUID id, SubmitJobDto job) {
        jdbi.useTransaction(handle -> {
            JobDao dao = handle.attach(JobDao.class);
            dao.insertJob(id, job.name(), job.jobData());
        });
    }
}
