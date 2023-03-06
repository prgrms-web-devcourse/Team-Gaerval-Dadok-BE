package com.dadok.gaerval.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@DisplayName("bookshelf repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfRepositoryTest {

	private final BookshelfRepository bookshelfRepository;

	private final JobRepository jobRepository;

	@DisplayName("findByIdWithUserAndJob 쿼리 테스트")
	@Test
	void findByIdWithUserAndJob() {
		bookshelfRepository.findByIdWithUserAndJob(100L);
	}

	@DisplayName("사용자의 책장 요약 조회")
	@Test
	void findSummaryByUser() {
		// Given
		var job = jobRepository.save(JobObjectProvider.backendJob());
		var user = UserObjectProvider.createKakaoUser();
		user.changeJob(job);
		var bookshelf = bookshelfRepository.save(Bookshelf.create(user));
		// When
		var res = bookshelfRepository.findSummaryById(bookshelf.getUser().getId());
		// Then
		assertThat(res.isPresent()).isTrue();
	}

	@DisplayName("사용자의 책장 요약 조회_empty 반환")
	@Test
	void findSummaryByUser_empty() {
		// When
		var res = bookshelfRepository.findSummaryById(3L);
		// Then
		assertThat(res.isEmpty()).isTrue();
	}

	@DisplayName("인기 책장 요약 list 조회 - findSuggestionsByJobGroup 쿼리테스트")
	@Test
	void findSuggestionsByJobGroup() {
		bookshelfRepository.findSuggestionsByJobGroup(JobGroup.DEVELOPMENT, 10L, 1);
	}
}