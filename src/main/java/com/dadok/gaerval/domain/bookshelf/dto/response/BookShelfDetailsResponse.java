package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public class BookShelfDetailsResponse {

	private boolean isFirst; // 첫번째냐

	private boolean isLast;  // 마지막이냐

	private boolean totalPages;  // 토탈페이지

	private boolean totalElements; // 데이터베이스 총 갯수

	private boolean size;  // 검색한 갯수

	private int number; // 페이지 넘버

	private int numberOfElements; // 검색한 총 갯수

	private boolean isEmpty; // 반환 값이 0개인가

	private List<BookInBookShelfResponse> books;

	public static class BookInBookShelfResponse {
		private Long bookId;

		private String title;

		private String author;

		private String isbn;

		private String contents;

		private String imageUrl;

		private String url;

		private String publisher;
	}

}
