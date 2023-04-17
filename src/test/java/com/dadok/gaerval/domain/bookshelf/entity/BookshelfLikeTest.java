package com.dadok.gaerval.domain.bookshelf.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class BookshelfLikeTest {

	private final User owner = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());

	private final User user = UserObjectProvider.createNaverUser();

	private final Bookshelf bookshelf = Bookshelf.create(owner);

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(owner, "id", 1L);
		ReflectionTestUtils.setField(user, "id", 2L);
	}

	@Test
	@DisplayName("create - 모든 필드가 유효 - 성공")
	void create_success() {
		assertDoesNotThrow(() -> {
			BookshelfLike.create(user, bookshelf);
		});
	}

	@Test
	@DisplayName("create - 책장이 null일 경우 - 실패")
	void create_bookshelfNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookshelfLike.create(user, null);
		});
	}

	@Test
	@DisplayName("create - 사용자가 null일 경우 - 실패")
	void create_userNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookshelfLike.create(null, bookshelf);
		});
	}

	@Test
	@DisplayName("create - 자신의 책장일경우 - 실패")
	void create_BookshelfOwner_fail() {
		assertThrows(BookshelfUserNotMatchedException.class, () -> {
			BookshelfLike.create(owner, bookshelf);
		});
	}

}