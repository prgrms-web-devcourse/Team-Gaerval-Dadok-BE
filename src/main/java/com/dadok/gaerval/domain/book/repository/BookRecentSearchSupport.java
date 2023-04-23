package com.dadok.gaerval.domain.book.repository;

import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.dadok.gaerval.domain.book.entity.BookRecentSearch;

public interface BookRecentSearchSupport {
	BookRecentSearchResponses findRecentSearches(Long userId, Long limit);

	void updateRecentSearchKeyword(BookRecentSearch bookRecentSearchData);

	boolean existsByKeywordAndUserId(String keyword, Long userId);
}

