package com.dadok.gaerval.domain.job.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class JobRepositoryTest {

	private final JobRepository jobRepository;

	@DisplayName("findByJobGroupAndJobName - String 타입으로 조회한다.")
	@Test
	void findByJobGroupAndJobName_asString() {
		String development = "DEVELOPMENT";
		String backendDevelop = "BACKEND_DEVELOPER";
		jobRepository.findByJobGroupAndJobName(development, backendDevelop);
	}

	@DisplayName("findByJobGroupAndJobName - enum 타입으로 조회한다.")
	@Test
	void findByJobGroupAndJobName_asEnum() {
		jobRepository.findByJobGroupAndJobName(JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER);
	}

}