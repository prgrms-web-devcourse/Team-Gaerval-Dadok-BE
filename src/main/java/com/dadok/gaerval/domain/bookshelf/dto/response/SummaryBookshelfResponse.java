package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record SummaryBookshelfResponse(Long bookshelfId, String bookshelfName, List<SummaryBookResponse> books) {

	public SummaryBookshelfResponse(Long bookshelfId, String bookshelfName, List<SummaryBookResponse> books) {
		this.bookshelfId = bookshelfId;
		this.bookshelfName = bookshelfName;
		if (books == null || books.get(0).bookId == null) {
			this.books = new ArrayList<>();
		} else {
			this.books = books.stream().limit(5).collect(Collectors.toList());
		}
	}

	public record SummaryBookResponse(
		Long bookId, String title, String imageUrl) {
	}
}
