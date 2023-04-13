package com.dadok.gaerval.domain.book.dto.request;

import com.dadok.gaerval.global.common.EnumType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortingPolicy implements EnumType {

	ACCURACY("accuracy", "정확도 순"),
	LATEST("latest", "최신순");

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
