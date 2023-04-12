package com.dadok.gaerval.domain.book.repository;

import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;

public interface BookRecentSearchSupport {
	BookRecentSearchResponses findRecentSearches(Long userId, Long limit);
}

