package com.dadok.gaerval.domain.bookshelf.entity;

import com.dadok.gaerval.global.common.EnumType;

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
}
