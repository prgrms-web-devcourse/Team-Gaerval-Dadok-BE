package com.dadok.gaerval.domain.book.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookCommentObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookRecentSearchSupportImplTest {

	private final BookRecentSearchRepository bookRecentSearchRepository;
	private final UserRepository userRepository;

	@DisplayName("findRecentSearches - 유저 id로 검색어 찾는 쿼리 테스트")
	@Test
	void existsByBookIdAndUserId() {
		bookRecentSearchRepository.findRecentSearches(BookCommentObjectProvider.userId,
			10L);
	}

	@Sql(scripts = {"/sql/user/users_and_job.sql","/sql/book_recent_search/insert_keyword_data.sql"}, executionPhase =
		Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("updateRecentSearchKeyword - 최근 검색어가 존재할 경우 검색일시를 업데이트한다.")
	@Test
	void updateRecentSearchKeyword_whenRecentSearchExists_thenUpdateSearchDateTime() {
		// When
		Optional<User> user = userRepository.findById(1L);
		bookRecentSearchRepository.updateRecentSearchKeyword(BookRecentSearch.create(user.get(),"테스트"));

		// Then
		assertTrue(user.isPresent());
	}

	@Test
	@DisplayName("existsByKeywordAndUserId - 유저 id와 키워드로 검색어 존재 여부 확인 테스트")
	void existsByKeywordAndUserIdTest() {
		// Given
		String keyword = "TestKeyword";
		Long userId = 10L;

		bookRecentSearchRepository.existsByKeywordAndUserId(keyword, userId);



	}
}