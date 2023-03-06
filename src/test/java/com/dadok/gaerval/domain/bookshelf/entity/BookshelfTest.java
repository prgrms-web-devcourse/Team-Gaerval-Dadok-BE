package com.dadok.gaerval.domain.bookshelf.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@DisplayName("Bookshelf 테스트")
class BookshelfTest {

	private final User user = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());

	@DisplayName("create - Bookshelf의 필드가 유효 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			Bookshelf.create(user);
		});
	}

	@DisplayName("create - Bookshelf의 필드가 유효 - 성공")
	@Test
	void create_success_field_check() {

		Bookshelf bookshelf = Bookshelf.create(user);

		assertThat(bookshelf)
			.hasFieldOrPropertyWithValue("name", user.getOauthNickname() + "님의 책장")
			.hasFieldOrPropertyWithValue("isPublic", true)
			.hasFieldOrPropertyWithValue("jobId", null)
			.hasFieldOrPropertyWithValue("user", user);
	}

	@DisplayName("changeIsPublic - 성공")
	@Test
	void changeIsPublic_success() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertDoesNotThrow(() -> {
			bookshelf.changeIsPublic(false);
		});
	}

	@DisplayName("changeIsPublic - isPublic이 null일 경우 - 실패")
	@Test
	void changeIsPublic_isPublicNull_fail() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertThrows(InvalidArgumentException.class, () -> bookshelf.changeIsPublic(null));
	}

	@DisplayName("create - user가 null일 경우 - 실패")
	@Test
	void create_UserNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> Bookshelf.create(null));
	}

	@DisplayName("addItem - item이 중복 추가 - 실패")
	@Test
	void addItem_success() {
		Bookshelf bookshelf = Bookshelf.create(user);
		BookshelfItem bookshelfItem = BookshelfItem.create(bookshelf, BookObjectProvider.createRequiredFieldBook());
		assertThrows(AlreadyContainBookshelfItemException.class, () -> bookshelf.addBookShelfItem(bookshelfItem));
	}

	@DisplayName("addItem - item이 null일 경우 - 실패")
	@Test
	void addItem_itemNull_fail() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertThrows(InvalidArgumentException.class, () -> bookshelf.addBookShelfItem(null));
	}

}