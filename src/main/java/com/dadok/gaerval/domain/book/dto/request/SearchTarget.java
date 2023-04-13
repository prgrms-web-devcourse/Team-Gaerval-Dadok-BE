package com.dadok.gaerval.domain.book.dto.request;

import com.dadok.gaerval.global.common.EnumType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchTarget implements EnumType {

	TITLE("title", "책 제목"),
	PERSON("person", "인명"),
	PUBLISHER("publisher", "출판사"),
	ISBN("isbn", "isbn");

	private final String name;
	private final String description;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
}
