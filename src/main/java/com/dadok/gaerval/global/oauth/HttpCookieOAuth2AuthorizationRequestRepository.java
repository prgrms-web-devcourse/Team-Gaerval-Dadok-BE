package com.dadok.gaerval.global.oauth;

import static java.util.Optional.*;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import com.dadok.gaerval.global.util.CookieUtil;

/**
 * Spring OAuth2는 기본적으로 HttpSessionOAuth2AuthorizationRequestRepository를 사용해 Authorization Request를 저장한다.
 * <p>
 * 우리는 JWT를 사용하므로, Session에 이를 저장할 필요가 없다.
 * 따라서 custom으로 구현한 HttpCookieOAuth2AuthorizationRequestRepository를 사용해 Authorization Reqeust를 Based64 encoded cookie에 저장한다
 */
public class HttpCookieOAuth2AuthorizationRequestRepository implements
	AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
	public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

	public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

	private static final int COOKIE_EXPIRATION_TIME = 180;


	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
			CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
			return;
		}

		CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest),
			COOKIE_EXPIRATION_TIME);

		String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);

		if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
			CookieUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin,
				COOKIE_EXPIRATION_TIME);
		}
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return this.loadAuthorizationRequest(request);
	}

	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
	}

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

		return getCookie(request)
			.map(this::getOAuth2AuthorizationRequest)
			.orElse(null);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(
		HttpServletRequest request,
		HttpServletResponse response
	) {

		return getCookie(request)
			.map(cookie -> {
				OAuth2AuthorizationRequest oauth2Request = getOAuth2AuthorizationRequest(cookie);
				clear(cookie, response);
				return oauth2Request;
			})
			.orElse(null);
	}

	private Optional<Cookie> getCookie(HttpServletRequest request) {

		return ofNullable(WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME));
	}

	private void clear(
		Cookie cookie,
		HttpServletResponse response
	) {

		cookie.setValue("");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	private OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(Cookie cookie) {

		return (OAuth2AuthorizationRequest)SerializationUtils.deserialize(
			Base64.getUrlDecoder()
				.decode(cookie.getValue())
		);
	}
}