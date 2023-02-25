package com.dadok.gaerval.testutil;

import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;

public class BookObjectProvider {

	private static final String title = "미움받을 용기";
	private static final String author = "기시미 이치로, 고가 후미타케";
	private static final String isbn = "9788996991342";
	private static final String contents = "인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.";
	private static final String url = "https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0";
	private static final String imageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038";
	private static final String apiProvider = "kakao";

	public static Book createRequiredFieldBook() {

		Book book = Book.create(title, author, isbn, contents, url, imageUrl, apiProvider);
		ReflectionTestUtils.setField(book, "id", 1L);
		return book;
	}
}
