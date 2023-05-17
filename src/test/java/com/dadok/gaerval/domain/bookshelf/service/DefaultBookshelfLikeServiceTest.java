package com.dadok.gaerval.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfLike;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfLikeRepository;
import com.dadok.gaerval.integration_util.IntegrationTest;

import lombok.extern.slf4j.Slf4j;

@Tag("Integration Test")
@Sql(scripts = {"/sql/bookshelf/bookshelf_data.sql"}, executionPhase =
	Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Slf4j
public class DefaultBookshelfLikeServiceTest extends IntegrationTest {

	@Autowired
	private DefaultBookshelfLikeService bookshelfLikeService;

	@Autowired
	private BookshelfLikeRepository bookshelfLikeRepository;

	private BookshelfLike bookshelfLike;

	@BeforeEach
	void setup() {
		bookshelfLikeService.createBookshelfLike(1L, 1001L);
		var res = bookshelfLikeRepository.findAll();
		bookshelfLike = res.get(0);
	}

	@Test
	@DisplayName("좋아요 삭제시 동시성 충돌 예외 방지 테스트")
	void deleteBookshelfLike_optimisticLockingFail() throws InterruptedException {
		int numberOfThreads = 10;
		ExecutorService service = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			service.execute(() -> {
				try {
					bookshelfLikeRepository.delete(bookshelfLike);
				} catch (ObjectOptimisticLockingFailureException e) {
					log.info("catch error :" + e.getMessage());
				}
				latch.countDown();
			});
		}
		latch.await();

		var find = bookshelfLikeRepository.findAll();
		assertThat(find.size()).isEqualTo(0);
	}

}
