package com.dadok.gaerval.testutil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.global.config.security.AuthProvider;

/**
 * @See OAuth2Attribute
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomOAuth2LoginUserSecurityContextFactory.class)
public @interface WithMockCustomOAuth2LoginUser {

	String username() default "username";

	String oauthNickname() default "oauthNickname"; // Provider에서 제공해주는 nickname

	String email() default "my@default.email";

	String picture() default "https://dadok.com";

	Role role() default Role.USER;

	String mockAccessToken() default "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJibGFja2RvZyIsImF1dGgiOlsiUk9MRV9VU0VSIl0sImVtYWlsIjoiYmxhY2tkb2dAYmxhY2";

	AuthProvider provider() default AuthProvider.KAKAO;

	String attributeKey() default "id";

	String oauthId() default "abcd12345678910";

	long userId() default 1L;

	//WithMockCustomOAuth2LoginUserSecurityContextFactory
}
