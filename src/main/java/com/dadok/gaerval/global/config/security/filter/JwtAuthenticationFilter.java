package com.dadok.gaerval.global.config.security.filter;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dadok.gaerval.global.config.security.jwt.JwtAuthenticationException;
import com.dadok.gaerval.global.config.security.jwt.JwtService;
import com.dadok.gaerval.global.error.ErrorCode;

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

		return Optional.ofNullable(bearerToken)
			.filter(t -> StringUtils.hasText(bearerToken) && t.startsWith(AUTHENTICATION_TYPE_BEARER))
			.map(t -> t.substring(7))
			.orElseThrow(() ->
				new JwtAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN));
	}

}
