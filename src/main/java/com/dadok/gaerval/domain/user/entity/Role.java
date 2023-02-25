package com.dadok.gaerval.domain.user.entity;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;

import com.dadok.gaerval.global.common.EnumType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : ysk
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Role implements GrantedAuthority, EnumType {

	@JsonProperty("USER")
	USER("ROLE_USER", "일반 사용자"),

	@JsonProperty("ADMIN")
	ADMIN("ROLE_ADMIN", "운영자");

	private final String key;
	private final String title;

	@Override
	public String getAuthority() {
		return key;
	}

	@JsonCreator
	public static Role of(String roleName) {

		return Arrays.stream(Role.values())
			.filter(role -> role.getKey().equals(roleName))
			.findFirst()
			//todo : 예외 변경
			.orElseThrow(() -> new IllegalArgumentException(roleName));
	}

	@Override
	public String getName() {
		return this.key;
	}

	@Override
	public String getDescription() {
		return this.title;
	}
}

