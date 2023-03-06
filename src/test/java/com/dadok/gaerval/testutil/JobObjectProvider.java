package com.dadok.gaerval.testutil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.job.dto.response.JobGroupResponse;
import com.dadok.gaerval.domain.job.dto.response.JobGroupResponse.JobNameResponse;
import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JobObjectProvider {

	public static JobResponses mockData() {

		JobGroup[] jobGroups = JobGroup.values();

		List<JobGroupResponse> jobGroupResponses = new ArrayList<>();

		List<JobNameResponse> jobNameResponses;

		for (JobGroup jobGroup : jobGroups) {

			jobNameResponses = new ArrayList<>();

			for (int j = 0; j < jobGroup.getJobNames().size(); j++) {
				JobGroup.JobName jobName = jobGroup.getJobNames().get(j);
				JobNameResponse jobNameResponse = new JobNameResponse(jobName.getJobName(),
					jobName, j + 1);

				jobNameResponses.add(jobNameResponse);
			}

			JobGroupResponse jobGroupResponse = new JobGroupResponse(jobGroup.getGroupName(), jobGroup, jobNameResponses);

			jobGroupResponses.add(jobGroupResponse);
		}

		return new JobResponses(jobGroupResponses);
	}

	public static List<Job> findAll() {
		JobGroup[] jobGroups = JobGroup.values();

		List<Job> jobs = new ArrayList<>();

		for (JobGroup jobGroup : jobGroups) {

			for (int i = 0; i < jobGroup.getJobNames().size(); i++) {
				JobGroup.JobName jobName = jobGroup.getJobNames().get(i);
				Job job = Job.create(jobGroup, jobName, i + 1);
				jobs.add(job);
			}
		}

		return jobs;
	}

	public static Job backendJob() {
		Job backendDeveloper = Job.create(JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);
		ReflectionTestUtils.setField(backendDeveloper, "id", 5L);
		return backendDeveloper;
	}

	public static Job frontendJob() {
		Job frontendDeveloper = Job.create(JobGroup.DEVELOPMENT, JobGroup.JobName.FRONTEND_DEVELOPER, 6);
		ReflectionTestUtils.setField(frontendDeveloper, "id", 6L);

		return frontendDeveloper;
	}

}

