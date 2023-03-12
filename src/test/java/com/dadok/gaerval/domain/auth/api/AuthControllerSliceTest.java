package com.dadok.gaerval.domain.auth.api;

import static com.dadok.gaerval.controller.document.utils.RequestCookiesSnippet.*;
import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.global.config.security.jwt.AuthService;
import com.dadok.gaerval.global.config.security.jwt.JwtProperties;
import com.dadok.gaerval.global.config.security.jwt.JwtProvider;

@WebMvcTest(controllers = AuthController.class)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
class AuthControllerSliceTest extends ControllerSliceTest {

	@Autowired
	private JwtProvider jwtProvider;

	@MockBean
	private AuthService authService;

	@TestConfiguration
	static class WebMvcTestConfig {

		@Bean
		public JwtProperties jwtProperties() {
			return new JwtProperties(
				"ZGFkb2stYXV0aC1qd3QtYWNjZXNzLXRva2VuLXNlY3JldC1rZXktIUAjLWRldmVsb3A=", 6000, "dadok");
		}

		@Bean
		public JwtProvider jwtProvider(JwtProperties jwtProperties) {
			return new JwtProvider(jwtProperties);
		}
	}


	@DisplayName("리프레시 토큰으로 액세스 토큰을 재발급 한다.")
	@Test
	void refreshAccessToken() throws Exception {
		//given
		Long userId = 1L;
		String reIssueAccessToken = jwtProvider.createAccessToken(10L,
			List.of(new SimpleGrantedAuthority(Role.USER.name())));

		String refreshToken = UUID.randomUUID().toString();

		Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setMaxAge(1209600);

		given(authService.reIssueAccessToken(refreshToken))
			.willReturn(reIssueAccessToken);

		//when
		mockMvc.perform(post("/api/auth/token")
			.characterEncoding(StandardCharsets.UTF_8)
			.cookie(refreshTokenCookie)
		).andExpect(status().isOk())
			.andDo(this.restDocs.document(
			customRequestHeaderCookies(
				cookieWithName("RefreshToken").description("리프레시 토큰 쿠키 이름")
			),
			responseHeaders(
				headerWithName("Authorization").description("Refresh 된 access Token")
			)
		));

	}

	@DisplayName("액세스토큰과 리프레시 토큰으로 로그아웃 한다.")
	@Test
	void logout() throws Exception {
		//given
		Long userId = 1L;
		String accessToken = jwtProvider.createAccessToken(userId,
			List.of(new SimpleGrantedAuthority(Role.USER.name())));
		String refreshToken = UUID.randomUUID().toString();

		Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "39731dc0-4482-4c96-a400-9cf0f0a88827");
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setMaxAge(1209600);

		willDoNothing()
			.given(authService).logout(accessToken, "39731dc0-4482-4c96-a400-9cf0f0a88827");

		//when
		mockMvc.perform(post("/api/auth/logout")
			.header(ACCESS_TOKEN_HEADER_NAME, AUTHENTICATION_TYPE_BEARER + " " + accessToken)
			.cookie(refreshTokenCookie)
		).andExpect(status().isOk())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description("액세스 토큰")
				),
				customRequestHeaderCookies(
					cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("리프레시 토큰 쿠키 이름")
				),
				responseHeaders(
					headerWithName("Set-Cookie").description("만료되어 제거된 리프레시 토큰 쿠키")
				)
			));
	}

}