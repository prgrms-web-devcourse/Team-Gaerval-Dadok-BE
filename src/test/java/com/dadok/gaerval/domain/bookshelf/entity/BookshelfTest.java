package com.dadok.gaerval.domain.bookshelf.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@DisplayName("Bookshelf 테스트")
class BookshelfTest {

	private final String name = "영지의 서재";
	private final Boolean isPublic = false;
	private final User user = UserObjectProvider.createKakaoUser();

	@DisplayName("create - Bookshelf의 모든 필드가 유효 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			Bookshelf.create(name, isPublic, user);
		});
	}

	@DisplayName("create - isPublic default 값 저장 - 성공")
	@Test
	void create_defaultPublic_success() {
		assertDoesNotThrow(() -> {
			Bookshelf.create(name, user);
		});
	}

	@DisplayName("create - name이 null일 경우 - 실패")
	@Test
	void create_nameNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> Bookshelf.create(null, isPublic, user));
	}

	@DisplayName("create - isPublic이 null일 경우 - 실패")
	@Test
	void create_isPublicNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> Bookshelf.create(name, null, user));
	}

	@DisplayName("create - user가 null일 경우 - 실패")
	@Test
	void create_UserNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> Bookshelf.create(name, isPublic, null));
	}

	@DisplayName("addItem - item이 중복 추가 - 실패")
	@Test
	void addItem_success() {
		Bookshelf bookshelf = Bookshelf.create(name, isPublic, user);
		BookshelfItem bookshelfItem = BookshelfItem.create(bookshelf, BookObjectProvider.createRequiredFieldBook());
		assertThrows(IllegalArgumentException.class, () -> bookshelf.addBookShelfItem(bookshelfItem));
	}

	@DisplayName("addItem - item이 null일 경우 - 실패")
	@Test
	void addItem_itemNull_fail() {
		Bookshelf bookshelf = Bookshelf.create(name, isPublic, user);
		assertThrows(IllegalArgumentException.class, () -> bookshelf.addBookShelfItem(null));
	}

}