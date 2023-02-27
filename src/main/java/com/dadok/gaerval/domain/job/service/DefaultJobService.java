package com.dadok.gaerval.domain.job.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.job.dto.response.JobResponse;
import com.dadok.gaerval.domain.job.dto.response.JobResponse.JobGroupResponse;
import com.dadok.gaerval.domain.job.dto.response.JobResponse.JobNameResponse;
import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultJobService implements JobService {

	private final JobRepository jobRepository;

	@Transactional(readOnly = true)
	@Override
	public JobResponses findAllWithAsc() {

		List<JobResponse> jobResponses = jobRepository.findAll()
			.stream()
			.collect(groupingBy(Job::getJobGroup, TreeMap::new, toList()))
			.entrySet()
			.stream()
			.map(e -> new JobResponse(new JobGroupResponse(e.getKey().getGroupName(), e.getKey().name()),
				e.getValue().stream()
					.map(jobName -> {
						JobGroup.JobName name = jobName.getJobName();
						return new JobNameResponse(name.getJobName(), name.getName(), jobName.getSortOrder());
					})
					.collect(toList()
					))).collect(toList());

		return new JobResponses(jobResponses);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Job> findBy(String jobGroup, String jobName) {
		return jobRepository.findByJobGroupAndJobName(jobGroup, jobName);
	}

	@Transactional(readOnly = true)
	@Override
	public Job getBy(JobGroup jobGroup, JobGroup.JobName jobName) {
		return jobRepository.findByJobGroupAndJobName(jobGroup, jobName)
			.orElseThrow(() -> new ResourceNotfoundException(Job.class));
	}

}
