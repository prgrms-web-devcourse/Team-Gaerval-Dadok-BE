package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@DisplayName("bookshelfLike repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfLikeRepositoryTest {

	private final BookshelfLikeRepository bookshelfLikeRepository;

	@Test
	@DisplayName("조회 - 책장과 사용자을 입력받아 entity 조회")
	void findByUserIdAndBookshelfId() {
		bookshelfLikeRepository.findByUserIdAndBookshelfId(2L, 4L);
	}

	@Test
	@DisplayName("책장과 사용자를 입력받아 존재 여부 확인")
	void existsByBookshelfIdAndUserId() {
		bookshelfLikeRepository.existsByBookshelfIdAndUserId(2L, 4L);
	}

}