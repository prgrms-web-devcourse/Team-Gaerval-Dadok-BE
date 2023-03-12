package com.dadok.gaerval.global.config.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dadok.gaerval.domain.auth.dto.response.LoginResponse;
import com.dadok.gaerval.domain.auth.token.RefreshToken;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.domain.auth.service.RefreshTokenService;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.integration_util.IntegrationTest;

class AuthServiceTest extends IntegrationTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@AfterEach
	void clean() {
		redisCleanUp();
	}

	@DisplayName("refreshToken으로 accessToken을 재발급 한다.")
	@Test
	void reIssueAccessToken_success() {
		//given
		Long userId = 10L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);

		//when
		String reIssueAccessToken = authService.reIssueAccessToken(refreshToken.getToken());

		//then
		Long parseUserId = jwtProvider.getId(reIssueAccessToken);
		assertEquals(userId, parseUserId);
	}

	@DisplayName("refreshToken이 존재하지 않으면 예외를 던지고 재발급에 실패한다.")
	@Test
	void reIssueAccessToken_notExists_fail() {
		//given
		String dosentExistsRefreshToken = UUID.randomUUID().toString();

		//when
		assertThrows(RefreshTokenAuthenticationNotFoundException.class,
			() -> authService.reIssueAccessToken(dosentExistsRefreshToken));
	}

	@DisplayName("refreshToken이 존재하지 않으면 예외를 던지고 재발급에 실패한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void reIssueAccessToken_empty_fail(String empty) {
		//when
		assertThrows(RefreshTokenAuthenticationNotFoundException.class,
			() -> authService.reIssueAccessToken(empty));
	}

	@DisplayName("userId와 authorities로 accessToken과 refreshToken을 발급한다.")
	@Test
	void login_success() {
		//given
		Long userId = 9999L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		//when
		LoginResponse loginResponse = authService.login(userId, roles);
		//then
		assertNotNull(loginResponse.accessToken());
		assertNotNull(loginResponse.refreshToken());
	}

	@DisplayName("logout을 요청하면 리프레시 토큰을 레디스에서 제거한다.")
	@Test
	void logout() {
		//given
		Long userId = 10L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);
		//when
		authService.logout("accessToken", refreshToken.getToken());
		//then
		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(refreshToken.getToken());
		assertTrue(refreshTokenOptional.isEmpty());
	}

}