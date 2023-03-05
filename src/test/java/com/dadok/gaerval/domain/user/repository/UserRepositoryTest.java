package com.dadok.gaerval.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.UserObjectProvider;

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

	@DisplayName("existsByNickname - query 확인- false")
	@Test
	void existsByNickname_query_false() {
		userRepository.existsByNickname(new Nickname("nickname"));
	}

	@DisplayName("existsByNickname - query 확인 - true")
	@Test
	void existsByNickname_query_true() {

		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("nickname"));
		userRepository.save(kakaoUser);

		userRepository.existsByNickname(kakaoUser.getNickname());
	}

}