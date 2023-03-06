package com.dadok.gaerval.domain.user.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class UserTest {

	private final AuthProvider kakao = AuthProvider.KAKAO;
	private final String kakaoAttributeKey = "id";
	private final Map<String, Object> kakaoAttributes = UserObjectProvider.attributes(kakao);
	private final OAuth2Attribute kakaoOauth2Attributes = OAuth2Attribute.of(kakao, kakaoAttributeKey, kakaoAttributes);
	private final UserAuthority userAuthority = UserAuthority.create(Role.USER);

	@DisplayName("createByOAuth - User의 모든 필드가 유효하다면 생성한다. gender가 null이면 NONE으로 생성한다.- 성공")
	@Test
	void create_NONEGender_success() {
		//given
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(kakao, kakaoAttributeKey, attributes);
		//when
		User user = assertDoesNotThrow(
			() -> User.createByOAuth(oAuth2Attribute, userAuthority));

		//then
		assertThat(user)
			.hasFieldOrPropertyWithValue("email", oAuth2Attribute.getEmail())
			.hasFieldOrPropertyWithValue("name", null)
			.hasFieldOrPropertyWithValue("oauthNickname", oAuth2Attribute.getName())
			.hasFieldOrPropertyWithValue("profileImage", oAuth2Attribute.getPicture())
			.hasFieldOrPropertyWithValue("authId", oAuth2Attribute.getOauthId())
			.hasFieldOrPropertyWithValue("authProvider", oAuth2Attribute.getAuthProvider())
			.hasFieldOrPropertyWithValue("authorities", List.of(userAuthority))
			.hasFieldOrPropertyWithValue("gender", Gender.NONE)
		;
	}

	@SuppressWarnings("unchecked")
	@DisplayName("createByOAuth - User의 모든 필드가 유효하다면 생성한다. gender가 존재한다. - 성공")
	@Test
	void create_withGender_success() {
		//given
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);

		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		kakaoAccount.put("gender", Gender.MALE.name());
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(kakao, kakaoAttributeKey, attributes);

		//when
		User user = assertDoesNotThrow(
			() -> User.createByOAuth(oAuth2Attribute, userAuthority));

		//then
		assertThat(user)
			.hasFieldOrPropertyWithValue("email", oAuth2Attribute.getEmail())
			.hasFieldOrPropertyWithValue("name", null)
			.hasFieldOrPropertyWithValue("oauthNickname", oAuth2Attribute.getName())
			.hasFieldOrPropertyWithValue("profileImage", oAuth2Attribute.getPicture())
			.hasFieldOrPropertyWithValue("authId", oAuth2Attribute.getOauthId())
			.hasFieldOrPropertyWithValue("authProvider", oAuth2Attribute.getAuthProvider())
			.hasFieldOrPropertyWithValue("authorities", List.of(userAuthority))
			.hasFieldOrPropertyWithValue("gender", Gender.MALE);
		;
	}

	@DisplayName("grantedAuthorities - User가 가지고 있는 권한들을 리턴한다 - 성공")
	@Test
	void grantedAuthorities_success() {
		//given
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(kakao, kakaoAttributeKey, attributes);
		User user = User.createByOAuth(oAuth2Attribute, userAuthority);
		//when
		Collection<GrantedAuthority> grantedAuthorities = user.grantedAuthorities();
		List<GrantedAuthority> grantedAuthoritiesList = (List<GrantedAuthority>)grantedAuthorities;
		GrantedAuthority grantedAuthority = grantedAuthoritiesList.get(0);

		//then
		assertThat(grantedAuthorities).size().isEqualTo(1);
		assertThat(grantedAuthority.getAuthority()).isEqualTo(Role.USER.getAuthority());
	}

	@DisplayName("changeJob - 유저의 Job이 바뀐다.")
	@Test
	void changeJob() {
		//given
		User user = UserObjectProvider.createKakaoUser();
		Job beforeUserJob = user.getJob();

		Job frontEndJob = JobObjectProvider.frontendJob();
		//when
		user.changeJob(frontEndJob);
		//then
		assertNotEquals(beforeUserJob, user.getJob());
		assertEquals(user.getJob(), frontEndJob);
	}

	@DisplayName("isSameNickname - 유저의 Nickname이 null일때 false 이다")
	@Test
	void isSameNickname_userNicknameNull_false() {
	    //given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Nickname nickname = new Nickname("kakaoUser");
	    // when & then
		assertFalse(kakaoUser.isSameNickname(nickname));
	}

	@DisplayName("isSameNickname - 유저의 Nickname이 nickname과 다르다면 false 이다")
	@Test
	void isSameNickname_userNickname_notSame_false() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Nickname nickname = new Nickname("kakaoUser");
		kakaoUser.changeNickname(nickname);

		Nickname diffNickname = new Nickname("diff");
		// when & then
		assertFalse(kakaoUser.isSameNickname(diffNickname));
	}

	@DisplayName("isSameNickname - nickname이 null이면 예외를 던진다")
	@Test
	void isSameNickname_nicknameNull_throw() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		// when & then
		assertThrows(InvalidArgumentException.class,
			() -> kakaoUser.isSameNickname(null));
	}

	@DisplayName("isSameNickname - 유저의 Nickname과 Nickname이 같다면 true 이다")
	@Test
	void isSameNickname_true() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Nickname nickname = new Nickname("kakaoUser");
		kakaoUser.changeNickname(nickname);

		Nickname sameNickname = new Nickname("kakaoUser");
		// when & then
		assertTrue(kakaoUser.isSameNickname(sameNickname));
	}

	@DisplayName("isSameJob - Job이 null이면 예외를 던진다.")
	@Test
	void isSameJob_jobNull_throw() {
	    //given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		//when & then
		assertThrows(InvalidArgumentException.class,
			() -> kakaoUser.isSameJob(null));
	}

	@DisplayName("isSameJob - 유저의 Job과 Job이 같다면 true 이다")
	@Test
	void isSameJob_true() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Job job = JobObjectProvider.backendJob();
		kakaoUser.changeJob(job);

		Job sameJob = JobObjectProvider.backendJob();
		//when & then
		assertTrue(kakaoUser.isSameJob(sameJob));
	}

	@DisplayName("isSameJob - 유저의 Job과 Job이 다르다면 false 이다")
	@Test
	void isSameJob_false() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Job job = JobObjectProvider.backendJob();
		kakaoUser.changeJob(job);

		Job diffJob = JobObjectProvider.frontendJob();
		//when & then
		assertFalse(kakaoUser.isSameJob(diffJob));
	}

	@DisplayName("isSameJob - 유저의 Job이 null이라면 false이다. ")
	@Test
	void isSameJob_userJobNull_false() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Job diffJob = JobObjectProvider.frontendJob();
		//when & then
		assertFalse(kakaoUser.isSameJob(diffJob));
	}

}