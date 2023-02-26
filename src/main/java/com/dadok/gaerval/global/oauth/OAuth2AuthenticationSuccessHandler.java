package com.dadok.gaerval.global.oauth;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static com.dadok.gaerval.global.oauth.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.dadok.gaerval.global.config.security.UserPrincipal;
import com.dadok.gaerval.global.config.security.jwt.JwtService;
import com.dadok.gaerval.global.util.CookieUtil;

import lombok.RequiredArgsConstructor;

/**
 * Success Handler에 진입했다는 것은, 로그인이 완료되었다는 뜻
 * 해당 클래스의 주요 기능은 크게 2가지이다.
 * <p>
 * 최초 로그인인지 확인
 * Access Token, Refresh Token 생성 및 발급
 * token을 포함하여 리다이렉트
 */
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final JwtService jwtService;

	public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws ServletException, IOException {
		// 최초 로그인이라면 회원가입 처리를 한다.

		if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
			String targetUrl = determineTargetUrl(request, response, userPrincipal);

			if (response.isCommitted()) {
				return;
			}

			getRedirectStrategy().sendRedirect(request, response, targetUrl);
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

	private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		UserPrincipal userPrincipal) {

		Optional<String> redirectUri = CookieUtil.getCookieBy(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		String targetUri = redirectUri.orElse(getDefaultTargetUrl());
		String accessToken = jwtService.createAccessToken(userPrincipal.getUserId(), userPrincipal.getAuthorities());
		// String refreshToken = generateRefreshToken(user);
		//todo : 리프레시 토큰 설계
		response.setHeader(ACCESS_TOKEN_HEADER_NAME, AUTHENTICATION_TYPE_BEARER + " " + accessToken);
		CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, 180);
		return UriComponentsBuilder.fromUriString(targetUri)
			.queryParam("access_token", accessToken)
			.build().toUriString();
	}

	// private String generateRefreshToken(User user) {
	// 	return jwtService.createRefreshToken(user.getEmail());
	// }

}
