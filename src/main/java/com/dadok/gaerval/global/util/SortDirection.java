package com.dadok.gaerval.global.util;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.data.domain.Sort;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortDirection implements EnumType {
	ASC("오름차순", Sort.Direction.ASC), DESC("내림차순", Sort.Direction.DESC);

	private final String description;

	private final Sort.Direction direction;

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDescription() {
		return description;
	}

	public Sort.Direction toDirection() {
		return this.direction;
	}

	@JsonCreator
	public static SortDirection of(String direction) {
		return Arrays.stream(values())
			.filter(sd -> Objects.equals(sd.name(), direction.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(direction, "direction"));
	}

}
