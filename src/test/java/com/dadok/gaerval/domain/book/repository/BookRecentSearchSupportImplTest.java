package com.dadok.gaerval.domain.book.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookCommentObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookRecentSearchSupportImplTest {

	private final BookRecentSearchRepository bookRecentSearchRepository;


	@DisplayName("findRecentSearches - 유저 id로 검색어 찾는 쿼리 테스트")
	@Test
	void existsByBookIdAndUserId() {
		bookRecentSearchRepository.findRecentSearches(BookCommentObjectProvider.userId,
			10L);
	}

}