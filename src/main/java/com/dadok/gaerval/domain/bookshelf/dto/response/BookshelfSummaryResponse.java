package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public class BookshelfSummaryResponse {

	String bookshelfName;

	Long bookshelfId;

	List<BookSummaryResponse> books;

	private static class BookSummaryResponse {

		Long bookId;

		String title;

		String author;

		String isbn;

		String imageUrl;

		boolean marked;
	}
}
