package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@DisplayName("bookshelf repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfRepositoryTest {

	private final BookshelfRepository bookshelfRepository;

	private final User user = UserObjectProvider.createKakaoUser();

	@Test
	void test() {
		bookshelfRepository.findAllByJob(JobGroup.GAME, PageRequest.of(0, 10), 234L);
	}
}