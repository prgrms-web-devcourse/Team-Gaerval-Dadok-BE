package com.dadok.gaerval.domain.auth.api;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.auth.dto.response.RefreshAccessTokenResponse;
import com.dadok.gaerval.domain.auth.exception.LogoutException;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.global.config.security.jwt.AuthService;
import com.dadok.gaerval.global.config.security.jwt.JwtProvider;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/token")
	public ResponseEntity<RefreshAccessTokenResponse> refreshAccessToken(
		HttpServletRequest request,
		HttpServletResponse response) {

		Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieBy(request, REFRESH_TOKEN_COOKIE_NAME);

		if (refreshTokenCookie.isEmpty()) {
			throw new RefreshTokenAuthenticationNotFoundException(ErrorCode.EMPTY_REFRESH_TOKEN);
		}

		String reIssueAccessToken = authService.reIssueAccessToken(refreshTokenCookie.get().getValue());

		response.setHeader(ACCESS_TOKEN_HEADER_NAME, AUTHENTICATION_TYPE_BEARER + " " + reIssueAccessToken);

		return ResponseEntity.ok(new RefreshAccessTokenResponse(reIssueAccessToken));
	}

	@PostMapping("/logout")
	public void logout(
		HttpServletRequest request,
		HttpServletResponse response) {
		Optional<String> accessTokenOptional = JwtProvider.resolveToken(request);

		Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieBy(request, REFRESH_TOKEN_COOKIE_NAME);

		if (accessTokenOptional.isEmpty() || refreshTokenCookie.isEmpty()) {
			throw new LogoutException(ErrorCode.MISMATCH_LOGOUT_AUTHENTICATION_TOKEN_NOT_FOUND);
		}

		authService.logout(accessTokenOptional.get(), refreshTokenCookie.get().getValue());

		CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
