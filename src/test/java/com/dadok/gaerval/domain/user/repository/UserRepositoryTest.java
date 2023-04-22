package com.dadok.gaerval.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
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

	@Sql(scripts = {"/sql/user/users_and_job.sql"}, executionPhase =
		Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("findAllByNicknames - 다독이란 이름을 가진 유저를 10명 조회한다.")
	@Test
	void findAllByNicknames_query_success() {
		String nameStr = "다독";
		Nickname nickname = new Nickname(nameStr);

		UserProfileResponses profileResponses = userRepository.findAllByNicknames(nickname, PageRequest.ofSize(10));

		List<UserProfileResponse> userProfileResponses = profileResponses.users();
		assertThat(userProfileResponses).hasSize(10);
		assertThat(userProfileResponses).allMatch(it -> it.nickname().contains(nameStr));
	}

}