package com.dadok.gaerval.domain.job.service;

import java.util.Optional;

import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.entity.JobGroup.JobName;

public interface JobService {

	JobResponses findAllWithAsc();

	Optional<Job> findBy(String jobGroup, String jobName);

	Job getBy(JobGroup jobGroup, JobName jobName);

}
