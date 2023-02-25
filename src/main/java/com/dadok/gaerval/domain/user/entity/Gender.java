package com.dadok.gaerval.domain.user.entity;

import com.dadok.gaerval.global.common.EnumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements EnumType {
	MALE("남자"), FEMALE("여자");

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
