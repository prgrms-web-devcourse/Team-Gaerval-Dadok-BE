package com.dadok.gaerval.domain.job.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dadok.gaerval.domain.job.dto.response.JobResponses;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.testutil.JobObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultJobServiceTest {

	@InjectMocks
	private DefaultJobService defaultJobService;

	@Mock
	private JobRepository jobRepository;

	@DisplayName("findAllWithAsc - 모든 JobGroup과 JobName을 그룹화하고 order에 맞게 정렬하여 반환한다. ")
	@Test
	void findAllWithAsc() {
		//given
		List<Job> jobs = JobObjectProvider.findAll();
		given(jobRepository.findAll())
			.willReturn(jobs);

		//when
		JobResponses jobResponses = defaultJobService.findAllWithAsc();

		//then
		long jobCount = jobResponses.jobGroups().stream()
			.mapToLong(jobResponse -> jobResponse.jobs().size())
			.sum();

		assertEquals(jobs.size(), jobCount);

		jobResponses.jobGroups()
			.forEach(jobResponse -> Assertions.assertThat(jobResponse.jobs())
				.extracting("order")
				.isSorted());

		verify(jobRepository).findAll();
	}

	@DisplayName("findBy JobGroup, JobName- String타입으로 찾아 반환한다")
	@Test
	void findByJobGroupAndJobName_asStringType() {
		//given
		Job job = Job.create(JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 1);
		String jobGroup = JobGroup.DEVELOPMENT.getName();
		String jobName = JobGroup.JobName.BACKEND_DEVELOPER.name();
		given(jobRepository.findByJobGroupAndJobName(jobGroup, jobName))
			.willReturn(Optional.of(job));

		//when
		Optional<Job> jobOptional = defaultJobService.findBy(jobGroup, jobName);
		//then

		assertTrue(jobOptional.isPresent());
		Job findJob = jobOptional.get();
		assertEquals(job, findJob);
		verify(jobRepository).findByJobGroupAndJobName(jobGroup, jobName);
	}

	@DisplayName("findBy JobGroup, JobName- Enum 타입으로 찾아 반환한다. 성공")
	@Test
	void findByJobGroupAndJobName_asEnumType_success() {
		//given
		Job job = Job.create(JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 1);
		JobGroup jobGroup = JobGroup.DEVELOPMENT;
		JobGroup.JobName jobName = JobGroup.JobName.BACKEND_DEVELOPER;

		given(jobRepository.findByJobGroupAndJobName(jobGroup, jobName))
			.willReturn(Optional.of(job));

		//when
		Job findJob = defaultJobService.getBy(jobGroup, jobName);

		//then
		assertEquals(job, findJob);
		verify(jobRepository).findByJobGroupAndJobName(jobGroup, jobName);
	}

	@DisplayName("findBy JobGroup, JobName- Enum 타입으로 찾지 못하고 예외를 던진다. fail")
	@Test
	void findByJobGroupAndJobName_asEnumType_fail() {
		//given
		JobGroup jobGroup = JobGroup.DEVELOPMENT;
		JobGroup.JobName jobName = JobGroup.JobName.BACKEND_DEVELOPER;

		given(jobRepository.findByJobGroupAndJobName(jobGroup, jobName))
			.willReturn(Optional.empty());

		//when
		assertThrows(ResourceNotfoundException.class,
			() -> defaultJobService.getBy(jobGroup, jobName));

		verify(jobRepository).findByJobGroupAndJobName(jobGroup, jobName);
	}


}