package com.dadok.gaerval.domain.book_group.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookGroupSupportImplTest {

	private final BookGroupRepository bookGroupRepository;

	@DisplayName("findAllBy 쿼리 테스트")
	@Test
	void findAllBy_query() {
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);
		bookGroupRepository.findAllBy(request);
	}

	@DisplayName("findAllByUser 쿼리 테스트")
	@Test
	void findAllByUser_query() {
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);
		bookGroupRepository.findAllByUser(request, 3L);
	}
}