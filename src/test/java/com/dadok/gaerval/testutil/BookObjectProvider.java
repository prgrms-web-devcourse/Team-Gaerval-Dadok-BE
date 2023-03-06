package com.dadok.gaerval.testutil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;

public class BookObjectProvider {

	public static final String title = "미움받을 용기";
	public static final String author = "기시미 이치로, 고가 후미타케";
	public static final String isbn = "9788996991342";
	public static final String contents = "인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.";
	public static final String url = "https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0";
	public static final String imageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038";
	public static final String apiProvider = "KAKAO";
	public static final String imageKey = "687f74fd-a612-4ec9-9ae5-8f7b7fe8e80f/photo-1606787364406-a3cdf06c6d0c.jpeg";
	public static final String publisher = "인플루엔셜";
	public static final long bookId = 123L;

	public static Book createRequiredFieldBook() {

		Book book = Book.create(title, author, isbn, contents, url, imageUrl, apiProvider, publisher);
		ReflectionTestUtils.setField(book, "id", bookId);
		return book;
	}

	public static Book createBook() {
		Book book = Book.create(title, author, isbn, contents, url, imageUrl, apiProvider, publisher);
		return book;
	}

	public static Book createAllFieldBook() {
		Book book = Book.create(title, author, isbn, contents, url, imageUrl, imageKey, apiProvider, publisher);
		ReflectionTestUtils.setField(book, "id", bookId);
		return book;
	}

	public static BookResponses mockBookData() {
		List<SearchBookResponse> bookList = new ArrayList<>();
		bookList.add(new SearchBookResponse(title, author, isbn, contents, url, imageUrl, apiProvider, publisher));
		return new BookResponses(bookList);
	}

	public static BookCreateRequest createBookCreateRequest() {
		return new BookCreateRequest(
			title,
			author,
			isbn,
			contents,
			url,
			imageUrl,
			publisher,
			apiProvider
		);
	}

	public static BookResponse createBookResponse() {
		return new BookResponse(title, author, isbn, contents, url, imageUrl, apiProvider, publisher, imageKey, bookId);
	}
}
