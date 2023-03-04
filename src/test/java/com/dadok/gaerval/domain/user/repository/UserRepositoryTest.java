package com.dadok.gaerval.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class UserRepositoryTest {

	private final UserRepository userRepository;

	private final JobRepository jobRepository;

	@DisplayName("findUserDetail - query 확인")
	@Test
	void findUserDetail_query() {
		userRepository.findUserDetail(1L);
	}

	@DisplayName("findUserProfile - query 확인")
	@Test
	void findUserProfile_query() {
		userRepository.findUserProfile(1L);
	}

	@DisplayName("existsByNickname - query 확인")
	@Test
	void existsByNickname_query() {
		userRepository.existsByNickname(new Nickname("nickname"));
	}

}