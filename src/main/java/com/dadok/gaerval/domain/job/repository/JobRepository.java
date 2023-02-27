package com.dadok.gaerval.domain.job.repository;

import static com.dadok.gaerval.domain.job.entity.JobGroup.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;

public interface JobRepository extends JpaRepository<Job, Long> {

	@Query(
		nativeQuery = true,
		value = "SELECT * FROM jobs j WHERE j.job_group = :jobGroup and j.job_name = :jobName")
	Optional<Job> findByJobGroupAndJobName(@Param("jobGroup") String jobGroup, @Param("jobName") String jobName);

	Optional<Job> findByJobGroupAndJobName(@Param("jobGroup") JobGroup jobGroup, @Param("jobName") JobName jobName);
}
