package com.dadok.gaerval.testutil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

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

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("username", oAuth2LoginUser.username());
		attributes.put("name", oAuth2LoginUser.username());
		attributes.put("email", oAuth2LoginUser.email());
		attributes.put("picture", oAuth2LoginUser.picture());

		OAuth2Attribute auth2Attribute = OAuth2Attribute.of(oAuth2LoginUser.provider().name(), oAuth2LoginUser.attributeKey(),
			attributes);

		UserAuthority userAuthority = UserAuthority.of(oAuth2LoginUser.role());
		User mockUser = User.createByOAuth(auth2Attribute, userAuthority);

		UserPrincipal userPrincipal = UserPrincipal.of(mockUser, attributes);

		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(userPrincipal, mockToken);

		context.setAuthentication(jwtAuthenticationToken);
		return context;
	}

}
