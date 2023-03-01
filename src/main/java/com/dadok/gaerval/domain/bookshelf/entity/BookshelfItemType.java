package com.dadok.gaerval.domain.bookshelf.entity;

import java.util.Arrays;
import java.util.Objects;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookshelfItemType implements EnumType {
	WISH("읽고 싶은 책"), READ("읽은 책");

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
	public static BookshelfItemType of(String type) {
		return Arrays.stream(values())
			.filter(t -> Objects.equals(t.name(), type.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidArgumentException(type, "BookshelfItemType"));
	}
}
