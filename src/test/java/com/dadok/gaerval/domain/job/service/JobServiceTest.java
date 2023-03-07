package com.dadok.gaerval.domain.job.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.integration_util.ServiceIntegration;

@Transactional
class JobServiceTest extends ServiceIntegration {

	@DisplayName("모든 JobGroup과 JobName을 그룹화하고 order에 맞게 ASC로 정렬하여 반환한다. ")
	@Test
	void findAllWithAsc() {
		//when
		JobResponses jobResponses = jobService.findAllWithAsc();

		//then
		jobResponses.jobGroups()
			.forEach(jobResponse -> Assertions.assertThat(jobResponse.jobs())
				.extracting("order")
				.isSorted());
	}

	@DisplayName("String타입으로 찾아 반환한다")
	@Test
	void findBy() {
		//given
		String jobGroup = JobGroup.DEVELOPMENT.getName();
		String jobName = JobGroup.JobName.BACKEND_DEVELOPER.name();
		//when
		Optional<Job> jobOptional = jobService.findBy(jobGroup, jobName);
		//then
		assertTrue(jobOptional.isPresent());
		Job findJob = jobOptional.get();
		assertEquals(JobGroup.DEVELOPMENT, findJob.getJobGroup());
		assertEquals(JobGroup.JobName.BACKEND_DEVELOPER, findJob.getJobName());
	}

	@DisplayName("Enum 타입으로 찾아 반환한다. 성공")
	@Test
	void getBy() {
		//given
		JobGroup jobGroup = JobGroup.DEVELOPMENT;
		JobGroup.JobName jobName = JobGroup.JobName.BACKEND_DEVELOPER;
		//when
		Job findJob = jobService.getBy(jobGroup, jobName);
		//then
		assertEquals(jobGroup, findJob.getJobGroup());
		assertEquals(jobName, findJob.getJobName());
	}

}