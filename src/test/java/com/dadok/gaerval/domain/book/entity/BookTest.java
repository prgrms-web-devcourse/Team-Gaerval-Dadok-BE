package com.dadok.gaerval.domain.book.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.BookObjectProvider;


class BookTest {


	@Test
	@DisplayName("도서 엔티티 생성에 성공한다.")
	void create_book_success() {

		// Given
		String title = "미움받을 용기";
		String author = "기시미 이치로, 고가 후미타케";
		String isbn = "9788996991342";
		String contents = "인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.";
		String url = "https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0";
		String imageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038";
		String apiProvider = "kakao";
		String publisher = "인플루엔셜";

		Book book = Book.create(title, author, isbn, contents, url, imageUrl, apiProvider, publisher);

		// When Then
		assertEquals(title, book.getTitle());
		assertEquals(author, book.getAuthor());
		assertEquals(isbn, book.getIsbn());
		assertEquals(contents, book.getContents());
		assertEquals(url, book.getUrl());
		assertEquals(imageUrl, book.getImageUrl());
		assertEquals(apiProvider, book.getApiProvider());
	}


	@Test
	@DisplayName("이미지키를 포함한 도서 엔티티 생성에 성공한다.")
	void create_with_image_key_success() {

		// Given
		String title = "미움받을 용기";
		String author = "기시미 이치로, 고가 후미타케";
		String isbn = "9788996991342";
		String contents = "인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.";
		String url = "https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0";
		String imageUrl = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038";
		String apiProvider = "kakao";
		String imageKey = "687f74fd-a612-4ec9-9ae5-8f7b7fe8e80f/photo-1606787364406-a3cdf06c6d0c.jpeg";
		String publisher = "인플루엔셜";

		Book book = Book.create(title, author, isbn, contents, url, imageUrl, imageKey, apiProvider, publisher);
		// When Then
		assertEquals(title, book.getTitle());
		assertEquals(author, book.getAuthor());
		assertEquals(isbn, book.getIsbn());
		assertEquals(contents, book.getContents());
		assertEquals(url, book.getUrl());
		assertEquals(imageUrl, book.getImageUrl());
		assertEquals(apiProvider, book.getApiProvider());
	}

	@Test
	@DisplayName("도서 엔티티 생성에 실패한다.")
	void create_book_failure() {

		// Given
		String emptyTitle = "";
		String author = "작가이름";
		String exceededIsbn = "978123456789012345678901234567890";
		String emptyContents = "";
		String url = "https://migu.world";
		String imageUrl = "";
		String apiProvider = "kakao";
		String publisher = "인플루엔셜";

		// When Then
		assertThrows(InvalidArgumentException.class, () -> new Book( emptyTitle, author, exceededIsbn, emptyContents, url, imageUrl, apiProvider, publisher));
	}


	@Test
	@DisplayName("생성된 도서 엔티티를 삭제 상태로 변경한다.")
	void change_book_deleted() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		// When
		book.changeDeleted(true);
		// Then
		assertTrue(book.isDeleted());
	}

	@Test
	@DisplayName("도서 제목이 변경된다.")
	void change_book_title() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newTitle = "새로운 도서 제목";
		// When
		book.changeTitle(newTitle);
		// Then
		assertEquals(newTitle, book.getTitle());
	}


	@Test
	@DisplayName("도서 저자가 변경된다.")
	void change_book_author() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newAuthor = "새로운 도서 저자";
		// When
		book.changeAuthor(newAuthor);
		// Then
		assertEquals(newAuthor, book.getAuthor());
	}



	@Test
	@DisplayName("도서 소개가 변경된다.")
	void change_book_contents() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newContents = "새로운 도서 소개";
		// When
		book.changeContents(newContents);
		// Then
		assertEquals(newContents, book.getContents());
	}

	@Test
	@DisplayName("도서 URL이 변경된다.")
	void change_book_url() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newUrl = BookObjectProvider.url;
		// When
		book.changeUrl(newUrl);
		// Then
		assertEquals(newUrl, book.getUrl());
	}

	@Test
	@DisplayName("도서 이미지 URL이 변경된다.")
	void change_book_imageUrl() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newImageUrl = BookObjectProvider.imageKey	;
		// When
		book.changeImageUrl(newImageUrl);
		// Then
		assertEquals(newImageUrl, book.getImageUrl());
	}


	@Test
	@DisplayName("도서 이미지 키가 변경된다.")
	void change_book_imageKey() {
		// Given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String newImageKey = BookObjectProvider.imageKey;
		// When
		book.changeImageKey(newImageKey);
		// Then
		assertEquals(newImageKey, book.getImageKey());
	}

	@Test
	@DisplayName("api provider가 변경된다.")
	void change_api_provider() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		String apiProvider = AuthProvider.NAVER.getName();
		// when
		book.changeApiProvider(apiProvider);
		// then
		assertEquals(apiProvider, book.getApiProvider());
	}
}