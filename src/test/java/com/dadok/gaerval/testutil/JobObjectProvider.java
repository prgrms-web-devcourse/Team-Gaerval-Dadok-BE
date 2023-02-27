package com.dadok.gaerval.testutil;

import java.util.ArrayList;
import java.util.List;

import com.dadok.gaerval.domain.job.dto.response.JobResponse;
import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JobObjectProvider {

	public static JobResponses mockData() {

		JobGroup[] jobGroups = JobGroup.values();

		List<JobResponse> jobResponses = new ArrayList<>();

		List<JobResponse.JobNameResponse> jobNameResponses = new ArrayList<>();
		for (JobGroup jobGroup : jobGroups) {

			jobNameResponses = new ArrayList<>();
			JobResponse.JobGroupResponse jobGroupResponse = new JobResponse.JobGroupResponse(jobGroup.getGroupName(),
				jobGroup.name());

			for (int j = 0; j < jobGroup.getJobNames().size(); j++) {
				JobGroup.JobName jobName = jobGroup.getJobNames().get(j);
				JobResponse.JobNameResponse jobNameResponse = new JobResponse.JobNameResponse(jobName.getJobName(),
					jobName.name(), j + 1);

				jobNameResponses.add(jobNameResponse);
			}

			jobResponses.add(new JobResponse(jobGroupResponse, jobNameResponses));
		}

		return new JobResponses(jobResponses);
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

}

