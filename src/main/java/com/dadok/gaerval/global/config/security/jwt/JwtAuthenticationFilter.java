package com.dadok.gaerval.global.config.security.jwt;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = resolveToken(request);

		if (StringUtils.hasText(accessToken)) {

			jwtService.validate(accessToken);

			Authentication authentication = jwtService.getAuthentication(accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}


	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(ACCESS_TOKEN_BEARER)) {
			return bearerToken.substring(7);
		}

		return null;
	}
}
