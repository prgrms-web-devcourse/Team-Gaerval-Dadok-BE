package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record SummaryBookshelfResponse(String bookshelfName,
									   Long bookshelfId,
									   List<SummaryBookResponse> books) {

	public record SummaryBookResponse(
		Long bookId, String title, String imageUrl) {
	}
}
