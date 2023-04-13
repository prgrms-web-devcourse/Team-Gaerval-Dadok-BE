package com.dadok.gaerval.domain.book.service;

import lombok.Getter;

@Getter
public class SaveKeywordEvent {
	private final Long userId;
	private final String keyword;

	public SaveKeywordEvent(Long userId, String keyword) {
		this.userId = userId;
		this.keyword = keyword;
	}
}