package com.dadok.gaerval.domain.book.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;

class BookRecentSearchTest {

	@DisplayName("BookRecentSearch 엔티티를 생성하는데 성공한다.")
	@Test
	void create_bookRecentSearch_success() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		String keyword = "이펙티브 자바";

		// when
		BookRecentSearch bookRecentSearch = BookRecentSearch.create(user, keyword);

		// then
		assertEquals(user, bookRecentSearch.getUser());
		assertEquals(keyword, bookRecentSearch.getKeyword());
	}

	@DisplayName("BookRecentSearch 엔티티 생성 실패 - 빈 검색어를 넣었을 때")
	@Test
	void create_bookRecentSearch_fail_keywordTooShort() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		String keyword = "";

		// when & then
		assertThrows(InvalidArgumentException.class, () -> BookRecentSearch.create(user, keyword), "검색어는 한글자 이상이어야 합니다.");
	}
}