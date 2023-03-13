package com.dadok.gaerval.global.config.security.jwt;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dadok.gaerval.domain.auth.dto.response.LoginResponse;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.domain.auth.service.RefreshTokenService;
import com.dadok.gaerval.domain.auth.token.RefreshToken;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

	public static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";

	public static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

	public static final String AUTHENTICATION_TYPE_BEARER = "Bearer";

	private final JwtProvider jwtProvider;

	private final RefreshTokenService refreshTokenService;

	public LoginResponse login(Long userId, Collection<GrantedAuthority> roles) {
		String accessToken = jwtProvider.createAccessToken(userId, roles);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, roles);

		return new LoginResponse(accessToken, refreshToken.getToken());
	}

	@Transactional(readOnly = true)
	public String reIssueAccessToken(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			throw new RefreshTokenAuthenticationNotFoundException();
		}

		RefreshToken findRefreshToken = refreshTokenService.getBy(refreshToken);

		return jwtProvider.createAccessToken(findRefreshToken.getUserId(),
			findRefreshToken.getRoles().stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList()));
	}

	public void logout(String accessToken, String refreshToken) {
		refreshTokenService.removeRefreshToken(refreshToken);
	}

}
