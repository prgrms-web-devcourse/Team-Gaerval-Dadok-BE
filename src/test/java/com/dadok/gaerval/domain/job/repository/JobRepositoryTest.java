package com.dadok.gaerval.domain.job.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.dadok.gaerval.repository.CustomDataJpaTest;

@CustomDataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class JobRepositoryTest {

	@Autowired
	private JobRepository jobRepository;

	@Test
	void test() {
		// Optional<Job> byId = jobRepository.findById(1L);
		//
		// Job job = byId.get();
		//
		// System.out.println(job.getJobs());
		//
		// Optional<Job> dataEnginner = jobRepository.findById(2L);
		//
		// System.out.println(dataEnginner.get().getJobs());
		//
		// System.out.println();
	}
}