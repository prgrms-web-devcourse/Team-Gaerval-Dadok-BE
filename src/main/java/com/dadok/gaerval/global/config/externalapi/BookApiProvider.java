package com.dadok.gaerval.global.config.externalapi;

import com.dadok.gaerval.global.common.EnumType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookApiProvider implements EnumType {

	NAVER("naver"),
	KAKAO("kakao"),
	ALADIN("aladin");

	private final String description;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDescription() {
		return this.description;
	}
}
