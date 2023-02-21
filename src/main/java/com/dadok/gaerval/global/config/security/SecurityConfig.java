package com.dadok.gaerval.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final String[] allowedApiUrls = {
		"/api/auth/signup", "/api/auth/login", "/docs", "/docs/index.html", "/docs/**"
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
	public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
		return http.authorizeRequests()
			.antMatchers(allowedApiUrls).permitAll()
			.antMatchers("/**").permitAll()
			.anyRequest().authenticated()
			.and()

			.formLogin().disable()

			.csrf().disable()

			.headers().disable()

			.httpBasic().disable()

			.rememberMe().disable()

			.logout().disable()

			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()

			.build();
	}

}