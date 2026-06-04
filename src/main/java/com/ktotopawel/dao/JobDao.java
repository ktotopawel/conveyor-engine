package com.ktotopawel.dao;

import com.ktotopawel.model.Job;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UseClasspathSqlLocator
public interface JobDao {
    @SqlUpdate
    void createTable();

    @SqlUpdate("insert into jobs (name, job_data) values (:name, :jobData)")
    void insertJob(@Bind("name") String name, @Bind("jobData") Map<String, Object> jobData);

    @SqlQuery
    @RegisterConstructorMapper(Job.class)
    Optional<Job> claimJob(@Bind("workerId") String workerId, @Bind("leaseDurationSeconds") Integer leaseDurationSeconds);

    @SqlUpdate
    void unclaimJob(@Bind("jobId") UUID jobId, @Bind("workerId") String workerId, @Bind("errorMessage") String errorMessage);

    @SqlUpdate("UPDATE jobs SET state = 'completed' WHERE id = :jobId AND owned_by_id = :workerId")
    void completeJob(@Bind("jobId") UUID jobId, @Bind("workerId") String workerId);
}
