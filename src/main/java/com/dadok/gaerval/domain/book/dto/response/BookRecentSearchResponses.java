package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

public record BookRecentSearchResponses(
	int count, // 결과 총 갯수
	boolean isEmpty, // 반환 값이 0개인가
	List<BookRecentSearchResponse> bookRecentSearchResponses
) {
	public BookRecentSearchResponses(List<BookRecentSearchResponse> bookRecentSearchResponses) {
		this(bookRecentSearchResponses.size(), bookRecentSearchResponses.isEmpty(), bookRecentSearchResponses);
	}
}
