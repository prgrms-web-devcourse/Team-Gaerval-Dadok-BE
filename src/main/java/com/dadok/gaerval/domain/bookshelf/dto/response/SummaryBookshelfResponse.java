package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SummaryBookshelfResponse {

	private final Long bookshelfId;

	private final String bookshelfName;

	private List<SummaryBookResponse> books;

	public void setBooks(List<SummaryBookResponse> books) {
		this.books = books;
	}

	public record SummaryBookResponse(
		Long bookId, String title, String imageUrl) {
	}
}
