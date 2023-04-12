package com.dadok.gaerval.domain.book.service;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class SaveKeywordEvent extends ApplicationEvent {
	private final Long userId;
	private final String keyword;

	public SaveKeywordEvent(Object source, Long userId, String keyword) {
		super(source);
		this.userId = userId;
		this.keyword = keyword;
	}
}