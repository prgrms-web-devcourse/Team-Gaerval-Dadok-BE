package com.dadok.gaerval.global.config.security.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dadok.gaerval.global.config.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull FilterChain filterChain) throws ServletException, IOException {

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<String> tokenOptional = JwtProvider.resolveToken(request);

			if (tokenOptional.isPresent()) {
				String accessToken = tokenOptional.get();
				jwtProvider.validate(accessToken);
				Authentication authentication = jwtProvider.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
		return false;
	}
}
