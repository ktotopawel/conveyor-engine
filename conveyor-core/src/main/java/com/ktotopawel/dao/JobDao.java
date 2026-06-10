package com.ktotopawel.dao;

import com.ktotopawel.model.Job;
import org.jdbi.v3.json.Json;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface JobDao {
    @SqlUpdate
    @UseClasspathSqlLocator
    void createTable();

    @SqlUpdate("insert into jobs (id, name, job_data) values (:id, :name, :jobData)")
    void insertJob(@Bind("id") UUID id, @Bind("name") String name, @Bind("jobData") @Json Map<String, Object> jobData);

    @SqlQuery
    @UseClasspathSqlLocator
    @RegisterConstructorMapper(Job.class)
    Optional<Job> claimJob(@Bind("workerId") String workerId, @Bind("leaseDurationSeconds") Integer leaseDurationSeconds);

    @SqlUpdate
    @UseClasspathSqlLocator
    void unclaimJob(@Bind("jobId") UUID jobId, @Bind("workerId") String workerId, @Bind("errorMessage") String errorMessage);

    @SqlUpdate("UPDATE jobs SET state = 'completed' WHERE id = :jobId AND owned_by_id = :workerId")
    void completeJob(@Bind("jobId") UUID jobId, @Bind("workerId") String workerId);
}
