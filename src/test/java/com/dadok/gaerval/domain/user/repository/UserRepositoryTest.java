package com.dadok.gaerval.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class UserRepositoryTest {

	private final UserRepository userRepository;

	@DisplayName("findUserDetail - query 확인")
	@Test
	void findUserDetail_query() {
		userRepository.findUserDetail(1L);
	}

}