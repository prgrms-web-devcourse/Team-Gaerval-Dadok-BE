package com.dadok.gaerval.domain.bookGroup.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.BookObjectProvider;

class BookGroupTest {

	private final Book book = BookObjectProvider.createRequiredFieldBook();

	@DisplayName("create - bookGroup 생성 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			BookGroup.create(2L, book, LocalDate.now(), LocalDate.now(), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - userId null일 경우 - 실패")
	@Test
	void create_userIdNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - book null일 경우 - 실패")
	@Test
	void create_bookNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, null, LocalDate.now(), LocalDate.now(), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - StartDate null일 경우 - 실패")
	@Test
	void create_startDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, null, LocalDate.now(), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - endDate null일 경우 - 실패")
	@Test
	void create_endDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), null, 2, "작은 모임", true);
		});
	}

	@DisplayName("create - startDate 지난 날짜인 경우 - 실패")
	@Test
	void create_startDateValid_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().minusDays(1), LocalDate.now(), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - endDate startDate보다 빠를 경우 - 실패")
	@Test
	void create_endDateValid_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().plusDays(1), LocalDate.now().minusDays(1), 2, "작은 모임", true);
		});
	}

	@DisplayName("create - introduce null일 경우 - 실패")
	@Test
	void create_introduceNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(), 2, null, true);
		});
	}

	@DisplayName("create - isPublic null일 경우 - 실패")
	@Test
	void create_isPublicNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(), 2, "작은 모임", null);
		});
	}
}