package com.dadok.gaerval.global.config.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class EmptyUserPrincipal extends UserPrincipal {

	public EmptyUserPrincipal() {
		super(0L, null, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
	}

	@Override
	public Long getUserId() {
		return null;
	}
}