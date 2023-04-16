package com.dadok.gaerval.domain.bookshelf.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class BookshelfLikeTest {

	private final User owner = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());

	private final User user = UserObjectProvider.createNaverUser();

	private final Bookshelf bookshelf = Bookshelf.create(owner);

	@Test
	@DisplayName("create")
	void test() {
		BookshelfLike bookshelfLike = BookshelfLike.create(user, bookshelf);
		ReflectionTestUtils.setField(bookshelfLike, "id", 5L);

		BookshelfLike bookshelfLike2 = BookshelfLike.create(user, bookshelf);
		ReflectionTestUtils.setField(bookshelfLike2, "id", 6L);

		System.out.println(bookshelf.getBookshelfLikes());
	}

}