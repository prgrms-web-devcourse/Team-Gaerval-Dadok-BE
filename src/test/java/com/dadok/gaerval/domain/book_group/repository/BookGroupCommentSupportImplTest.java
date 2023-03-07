package com.dadok.gaerval.domain.book_group.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookGroupCommentSupportImplTest {

	private final BookGroupCommentRepository bookGroupCommentRepository;

	@DisplayName("existsBy - id로 존재여부 테스트")
	@Test
	void existsBy() {
		bookGroupCommentRepository.existsBy(1L);
	}

	@DisplayName("findAllBy - groupId와 userId로 모임 코멘트 조회")
	@Test
	void findAllBy() {
		BookGroupCommentSearchRequest request = new BookGroupCommentSearchRequest(10, null, null);
		bookGroupCommentRepository.findAllBy(request, 123L, 123L);
	}
}