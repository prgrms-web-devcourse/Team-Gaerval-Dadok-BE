package com.dadok.gaerval.global.config.security;

import static org.apache.commons.lang3.CharEncoding.*;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.dadok.gaerval.global.config.security.filter.ExceptionHandlingFilter;
import com.dadok.gaerval.global.config.security.filter.JwtAuthenticationFilter;
import com.dadok.gaerval.global.config.security.jwt.JwtAccessDeniedHandler;
import com.dadok.gaerval.global.config.security.jwt.JwtAuthenticationEntryPoint;
import com.dadok.gaerval.global.config.security.jwt.JwtService;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;
import com.dadok.gaerval.global.error.response.ErrorResponse;
import com.dadok.gaerval.global.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.dadok.gaerval.global.oauth.OAuth2AuthenticationFailureHandler;
import com.dadok.gaerval.global.oauth.OAuth2AuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	private final CustomOAuth2UserService customOAuth2UserService;

	private final ObjectMapper objectMapper;

	private final String[] allowedApiUrls = {
		"/docs", "/docs/index.html", "/docs/**"
	};

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers(allowedApiUrls);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain httpSecurity(
		HttpSecurity http,
		JwtService jwtService
		) throws Exception {
		http
			.formLogin().disable()

			.csrf().disable()

			.headers().disable()

			.httpBasic().disable()

			.rememberMe().disable()

			.logout().disable()

			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()
			.antMatchers(allowedApiUrls).permitAll()
			.antMatchers("/oauth2/authorize/**", "/login/oauth2/code/**", "/docs/index.html", "/docs/**").permitAll()
			.anyRequest().permitAll()
			// .anyRequest().hasAnyRole("ROLE_ANONYMOUS", "ROLE_USER", "ROLE_ADMIN")
			.and()
			.anonymous().authorities("ROLE_ANONYMOUS")
			.and()
			// oauth
			.oauth2Login()
			.authorizationEndpoint().baseUri("/oauth2/authorize") // 클라이언트에서 의 접근 uri
			.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository())

			.and()
			.userInfoEndpoint()
			.userService(customOAuth2UserService)

			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler)
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
			.accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper))

		;

		http.addFilterBefore(new JwtAuthenticationFilter(jwtService), OAuth2AuthorizationRequestRedirectFilter.class);
		http.addFilterBefore(new ExceptionHandlingFilter(objectMapper), JwtAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(
		JdbcOperations jdbcOperations,
		ClientRegistrationRepository clientRegistrationRepository
	) {

		return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
	}

	public static void setErrorResponse(HttpServletRequest request, HttpServletResponse response,
		RuntimeException re, ObjectMapper objectMapper) throws IOException {

		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		ErrorResponse errorResponse = ErrorResponse.of(httpStatus, re.getMessage(), request.getRequestURI());

		if (re instanceof UnAuthenticationException e) {
			ErrorCode errorCode = e.getErrorCode();
			httpStatus = errorCode.getStatus();

			errorResponse = ErrorResponse.of(httpStatus, e.getMessage(), request.getRequestURI());
		} else if (re instanceof AccessDeniedException e) {
			httpStatus = HttpStatus.FORBIDDEN;
			errorResponse = ErrorResponse.of(httpStatus, e.getMessage(), request.getRequestURI());
		} else if (re instanceof AuthenticationException e) {
			httpStatus = HttpStatus.UNAUTHORIZED;
			errorResponse = ErrorResponse.of(httpStatus, e.getMessage(), request.getRequestURI());

		}

		response.setStatus(httpStatus.value());
		response.setContentType("application/json");
		response.setCharacterEncoding(UTF_8);

		response.getWriter()
			.print(objectMapper.writeValueAsString(
				new ResponseEntity<>(errorResponse, httpStatus)));
	}

}