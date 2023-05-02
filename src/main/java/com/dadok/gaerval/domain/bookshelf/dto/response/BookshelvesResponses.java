package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Slice;

public record BookshelvesResponses(
	Boolean isFirst,
	Boolean isLast,
	Boolean hasNext,
	int count,
	Boolean isEmpty,
	List<BookShelfSummaryResponse> bookshelfResponses
) {
	public BookshelvesResponses(Slice<BookShelfSummaryResponse> slice) {
		this(slice.isFirst(), slice.isLast(), slice.hasNext(), slice.getNumberOfElements(), slice.isEmpty(),
			slice.getContent());
	}

	public static BookshelvesResponses empty() {
		return new BookshelvesResponses(
			true, true, false, 0, true, Collections.emptyList()
		);
	}
}
