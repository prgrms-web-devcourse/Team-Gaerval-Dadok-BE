package com.dadok.gaerval.domain.book.dto.request;

import java.util.Arrays;
import java.util.Objects;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BestSellerSearchRange implements EnumType {
	WEEKLY("주간"),
	MONTHLY("월간"),
	YEARLY("연간");

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
	public static BestSellerSearchRange of(String range) {
		return Arrays.stream(values())
			.filter(sd -> Objects.equals(sd.name(), range.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(range, "range"));
	}
}
