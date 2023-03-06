package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record SuggestionBookshelvesByJobGroupResponses(String jobGroup,
													   List<BookShelfSummaryResponse> bookshelfResponses) {
}

