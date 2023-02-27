package com.dadok.gaerval.domain.job.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class JobGroupTest {

	@DisplayName("findSubJob - String jobName으로 enum JobName을 찾는다. ")
	@Test
	void findSubJob_success() {
		//given
		String javaJobName = "JAVA_DEVELOPER";
		//when
		JobGroup.JobName subJob = JobGroup.findSubJob(javaJobName);

		//then
		assertEquals(javaJobName, subJob.name());
	}

	@DisplayName("findSubJob - 일치하는 JobnName이 없으면 예외를 던진다. ")
	@ParameterizedTest
	@ValueSource(strings = {"javadeveloper", "java", "reactor", "reacctt"})
	void findSubJob_fail(String invalidJobName) {
		assertThrows(InvalidArgumentException.class, () -> JobGroup.findSubJob(invalidJobName));
	}

}