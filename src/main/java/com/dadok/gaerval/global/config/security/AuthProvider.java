package com.dadok.gaerval.global.config.security;

import java.util.stream.Stream;

import com.dadok.gaerval.global.common.EnumType;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthProvider implements EnumType {

	NAVER("naver"), KAKAO("kakao");

	private final String registrationId;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDescription() {
		return this.registrationId;
	}

	@JsonCreator
	public static AuthProvider of(String registrationId) {
		return Stream.of(values())
			.filter(v -> v.toString().equalsIgnoreCase(registrationId))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}

}
