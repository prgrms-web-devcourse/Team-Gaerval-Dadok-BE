package com.dadok.gaerval.domain.job.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.job.dto.response.JobGroupResponse;
import com.dadok.gaerval.domain.job.dto.response.JobGroupResponse.JobNameResponse;
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

		List<JobGroupResponse> jobGroupResponses = jobRepository.findAll()
			.stream()
			.collect(groupingBy(Job::getJobGroup, TreeMap::new, toList()))
			.entrySet()
			.stream()
			.map(job -> {
				JobGroup jobGroup = job.getKey();
				List<Job> jobs = job.getValue();

				return new JobGroupResponse(jobGroup.getGroupName(), jobGroup,
					jobs.stream().map(j -> {
						JobGroup.JobName jobName = j.getJobName();
						return new JobNameResponse(jobName.getJobName(), jobName, j.getSortOrder());
					}).collect(toList()));
			}).collect(toList());

		return new JobResponses(jobGroupResponses);
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
