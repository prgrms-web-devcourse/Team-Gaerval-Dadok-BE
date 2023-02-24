package com.dadok.gaerval.domain.user.dto.response;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public record LoginSuccessResponse(
	boolean hasJob,
	Long userId,
	Collection<GrantedAuthority> roles,
	String profileImage,
	String email
) {
}
