package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record PopularBookshelfOfJobResponses(String japGroup, int size,
											 List<BookshelfSummaryResponse> bookshelfResponses) {
}
