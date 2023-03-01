package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@DisplayName("bookshelf repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfRepositoryTest {

	private final BookshelfRepository bookshelfRepository;

	@DisplayName("인기 책장 요약 list 조회")
	@Test
	void findAllByJob() {
		bookshelfRepository.findAllByJob(JobGroup.GAME,
			PageRequest.of(0, 10, Sort.by(Sort.Order.desc("bookshelfItems.size"))),
			234L);
	}

	@DisplayName("사용자의 책장 요약 조회")
	@Test
	void findByUser() {
		bookshelfRepository.findByUser(1L);
	}

}