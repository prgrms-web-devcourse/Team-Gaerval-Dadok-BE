package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record SuggestionBookshelvesResponses(
	List<BookShelfSummaryResponse> bookshelfResponses
) {
}
