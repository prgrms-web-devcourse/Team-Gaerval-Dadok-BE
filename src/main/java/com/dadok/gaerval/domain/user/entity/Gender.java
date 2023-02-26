package com.dadok.gaerval.domain.user.entity;

import java.util.Arrays;
import java.util.Objects;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements EnumType {

	MALE("남자"),
	FEMALE("여자"),
	NONE("미설정");

	private final String description;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@JsonCreator
	public static Gender of(String gender) {
		return Arrays.stream(values())
			.filter(g -> Objects.nonNull(gender))
			.filter(g -> Objects.equals(g.name(), gender.toUpperCase())
				|| (gender.length() == 1 && Objects.equals(g.name().charAt(0), Character.toUpperCase(gender.charAt(0))))
			)
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(gender, "gender"));
	}

}
