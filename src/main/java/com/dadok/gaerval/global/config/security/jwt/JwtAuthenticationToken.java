package com.dadok.gaerval.global.config.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.dadok.gaerval.global.config.security.UserPrincipal;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;

	private final String token;

	public JwtAuthenticationToken(UserPrincipal userPrincipal, String token) {
		super(userPrincipal.getAuthorities());
		this.principal = userPrincipal;
		this.token = token;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.token;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
