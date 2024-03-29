package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record BookShelfSummaryResponse(
	Long bookshelfId,
	String bookshelfName,
	List<BookSummaryResponse> books,
	Integer likeCount
) {

	static final int BOOK_VIEW_LIMIT = 4;

	public BookShelfSummaryResponse(Long bookshelfId, String bookshelfName, List<BookSummaryResponse> books,
		Integer likeCount) {
		this.bookshelfId = bookshelfId;
		this.bookshelfName = bookshelfName;
		this.likeCount = likeCount;
		if (books == null || books.get(0).bookId == null) {
			this.books = new ArrayList<>();
		} else {
			this.books = books.stream().limit(BOOK_VIEW_LIMIT).collect(Collectors.toList());
		}
	}

	public record BookSummaryResponse(
		Long bookId, String title, String imageUrl) {
	}
}
