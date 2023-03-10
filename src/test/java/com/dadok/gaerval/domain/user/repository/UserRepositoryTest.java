package com.dadok.gaerval.domain.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class UserRepositoryTest {

	private final UserRepository userRepository;

	private final JobRepository jobRepository;

	private final AuthorityRepository authorityRepository;

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
		Authority authority = authorityRepository.save(Authority.create(Role.USER));
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User kakaoUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		kakaoUser.changeNickname(new Nickname("change"));
		userRepository.saveAndFlush(kakaoUser);


		userRepository.existsByNickname(kakaoUser.getNickname());
	}

}