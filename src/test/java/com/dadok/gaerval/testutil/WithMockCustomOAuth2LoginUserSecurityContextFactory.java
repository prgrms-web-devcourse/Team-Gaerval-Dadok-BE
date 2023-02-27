package com.dadok.gaerval.testutil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.global.config.security.UserPrincipal;
import com.dadok.gaerval.global.config.security.jwt.JwtAuthenticationToken;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

/**
 * accsstoken이 필요할 시, (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication()
 * @See JwtAuthenticationToken
 */
public class WithMockCustomOAuth2LoginUserSecurityContextFactory
	implements WithSecurityContextFactory<WithMockCustomOAuth2LoginUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomOAuth2LoginUser oAuth2LoginUser) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final String mockToken = oAuth2LoginUser.mockAccessToken();

		Map<String, Object> attributes = attributes(oAuth2LoginUser);

		OAuth2Attribute auth2Attribute = OAuth2Attribute.of(oAuth2LoginUser.provider(), oAuth2LoginUser.attributeKey(),
			attributes);

		UserAuthority userAuthority = UserAuthority.create(oAuth2LoginUser.role());
		User mockUser = User.createByOAuth(auth2Attribute, userAuthority);
		ReflectionTestUtils.setField(mockUser, "id", oAuth2LoginUser.userId());
		ReflectionTestUtils.setField(mockUser, "name", oAuth2LoginUser.username());

		UserPrincipal userPrincipal = UserPrincipal.of(mockUser, attributes);

		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(userPrincipal, mockToken);

		context.setAuthentication(jwtAuthenticationToken);
		return context;
	}

	private Map<String, Object> attributes( WithMockCustomOAuth2LoginUser oAuth2LoginUser) {
		Map<String, Object> attributes = new HashMap<>();

		switch (oAuth2LoginUser.provider()) {
			case KAKAO -> {
				Map<String, Object> kakaoAccount = new HashMap<>();
				Map<String, Object> kakaoProfile = new HashMap<>();

				kakaoAccount.put("email", oAuth2LoginUser.email());

				kakaoProfile.put("nickname", oAuth2LoginUser.nickname());
				kakaoProfile.put("profile_image_url", oAuth2LoginUser.picture());

				attributes.put("kakao_account", kakaoAccount);
				kakaoAccount.put("profile", kakaoProfile);
				attributes.put("id", oAuth2LoginUser.oauthId());

				return attributes;
			}
			case NAVER -> {
				Map<String, Object> response = new HashMap<>();
				response.put("name", oAuth2LoginUser.nickname());
				response.put("email", oAuth2LoginUser.email());
				response.put("profile_image", oAuth2LoginUser.picture());
				attributes.put("id", oAuth2LoginUser.oauthId());
				attributes.put("response", response);
				return attributes;
			}
		}

		return attributes;
	}


}
