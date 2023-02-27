package com.dadok.gaerval.domain.job.repository;

import static com.dadok.gaerval.domain.job.entity.JobGroup.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;

public interface JobRepository extends JpaRepository<Job, Long> {

	Optional<Job> findByJobGroupAndJobName(@Param("jobGroup") String jobGroup, @Param("JobName") String jobName);

	Optional<Job> findByJobGroupAndJobName(@Param("jobGroup") JobGroup jobGroup, @Param("jobName") JobName jobName);
}
