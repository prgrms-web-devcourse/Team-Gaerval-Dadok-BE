package com.dadok.gaerval.global.config.externalapi;

import com.dadok.gaerval.domain.book.dto.request.SearchTarget;

import reactor.core.publisher.Flux;

public interface ExternalBookApiOperations {
	Flux<String> searchBooks(String query, int page, int size, String sort);

	Flux<String> searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort);
}
