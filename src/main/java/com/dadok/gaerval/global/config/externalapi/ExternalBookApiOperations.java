package com.dadok.gaerval.global.config.externalapi;

import com.dadok.gaerval.domain.book.dto.request.SearchTarget;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;

public interface ExternalBookApiOperations {
	BookResponses searchBooks(String query, int page, int size, String sort);

	BookResponses searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort);
}
