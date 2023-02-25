package com.dadok.gaerval.testutil;

import com.dadok.gaerval.domain.book.entity.Book;

public class BookObjectProvider {

	public static Book createRequiredFieldBook() {
		return  Book.requiredBuilder()
			.id(1L)
			.title("미움받을 용기")
			.author("기시미 이치로, 고가 후미타케")
			.isbn("9788996991342")
			.contents("인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.")
			.url("https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0")
			.url("https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038")
			.apiProvider("kakao")
			.build();
	}
}
