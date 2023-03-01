package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record SummaryBookshelfResponse(Long bookshelfId, String bookshelfName, List<SummaryBookResponse> books) {

	public record SummaryBookResponse(
		Long bookId, String title, String imageUrl) {
	}
}
