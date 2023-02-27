package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public class SummaryBookshelfResponse {

	String bookshelfName;

	Long bookshelfId;

	List<SummaryBookResponse> books;

	private static class SummaryBookResponse {

		Long bookId;

		String title;

		String imageUrl;
	}
}
