package com.dadok.gaerval.domain.user.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.testutil.UserObjectProvider;

class UserTest {

	private AuthProvider kakao = AuthProvider.KAKAO;
	private String kakaoAttributeKey = "id";

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
			.hasFieldOrPropertyWithValue("nickname", oAuth2Attribute.getName())
			.hasFieldOrPropertyWithValue("profileImage", oAuth2Attribute.getPicture())
			.hasFieldOrPropertyWithValue("authId", oAuth2Attribute.getOauthId())
			.hasFieldOrPropertyWithValue("authProvider", oAuth2Attribute.getAuthProvider())
			.hasFieldOrPropertyWithValue("authorities", List.of(userAuthority))
			.hasFieldOrPropertyWithValue("gender", Gender.NONE)
		;
	}

	@DisplayName("createByOAuth - User의 모든 필드가 유효하다면 생성한다. gender가 존재한다. - 성공")
	@Test
	void create_withGender_success() {
		//given
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);

		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		kakaoAccount.put("gender", Gender.MALE.name());
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(kakao, kakaoAttributeKey, attributes);

		//when
		User user = assertDoesNotThrow(
			() -> User.createByOAuth(oAuth2Attribute, userAuthority));

		//then
		assertThat(user)
			.hasFieldOrPropertyWithValue("email", oAuth2Attribute.getEmail())
			.hasFieldOrPropertyWithValue("name", null)
			.hasFieldOrPropertyWithValue("nickname", oAuth2Attribute.getName())
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

}