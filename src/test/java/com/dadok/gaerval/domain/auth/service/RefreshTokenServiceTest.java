package com.dadok.gaerval.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dadok.gaerval.domain.auth.token.RefreshToken;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.integration_util.IntegrationTest;

class RefreshTokenServiceTest extends IntegrationTest {

	@AfterEach
	void clean() {
		redisCleanUp();
	}

	@DisplayName("유저 이름과 권한을 담은 리프레시 토큰을 레디스에 저장한다.")
	@Test
	void createRefreshToken() {
		//given
		Long userId = 10L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		//when
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);
		//then
		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(refreshToken.getToken());
		assertTrue(refreshTokenOptional.isPresent());
		RefreshToken findRefreshToken = refreshTokenOptional.get();
		assertEquals(10L, findRefreshToken.getUserId());
		assertEquals(roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
			findRefreshToken.getRoles());
	}

	@DisplayName("저장된 리프레시 토큰을 찾아온다.")
	@Test
	void getBy_success() {
		//given
		Long userId = 10L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);
		//when
		RefreshToken findRefreshToken = refreshTokenService.getBy(refreshToken.getToken());

		//then
		assertEquals(findRefreshToken.getUserId(), refreshToken.getUserId());
		assertEquals(findRefreshToken.getToken(), refreshToken.getToken());
		assertEquals(refreshToken.getRoles(), findRefreshToken.getRoles());
	}

	@DisplayName("저장된 리프레시 토큰이 없다면 예외를 던진다.")
	@Test
	void getBy_throw() {
		//given
		String randomUUID = UUID.randomUUID().toString();
		//when
		assertThrows(RefreshTokenAuthenticationNotFoundException.class,
			() -> refreshTokenService.getBy(randomUUID));
	}

	@DisplayName("refreshToken 파라미터가 존재하지 않으면 예외를 던지고 재발급에 실패한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void getBy_empty_fail(String empty) {
		//when
		assertThrows(RefreshTokenAuthenticationNotFoundException.class,
			() -> refreshTokenService.getBy(empty));
	}

	@DisplayName("리프레시 토큰을 레디스에서 제거한다.")
	@Test
	void removeRefreshToken() {
		//given
		Long userId = 10L;
		Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(Role.USER.name()));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);
		//when
		refreshTokenService.removeRefreshToken(refreshToken.getToken());
		//then
		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(refreshToken.getToken());
		assertTrue(refreshTokenOptional.isEmpty());
	}

	@DisplayName("존재하지 않는 리프레시 토큰을 제거하려고 해도 문제가 없다.")
	@Test
	void removeRefreshToken_notExistsToken() {
		//given
		String notExistsRefreshToken = UUID.randomUUID().toString();
		//when
		refreshTokenService.removeRefreshToken(notExistsRefreshToken);
	}

}