package com.ktotopawel.repository;

import com.ktotopawel.dao.JobDao;
import com.ktotopawel.model.Job;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class JobRepository {

    private final Jdbi jdbi;
    private final Logger logger = LoggerFactory.getLogger(JobRepository.class);

    public void save(Job job) {
        jdbi.useTransaction(handle -> {
            JobDao dao = handle.attach(JobDao.class);

            logger.debug("Inserting job: id={}, name={}, data={}", job.id(), job.name(), job.jobData());

            dao.insertJob(job.id(), job.name(), job.jobData());
        });
    }
}
