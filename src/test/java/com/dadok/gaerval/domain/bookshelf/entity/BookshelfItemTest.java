package com.dadok.gaerval.domain.bookshelf.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.testutil.BookObjectProvider;

@DisplayName("BookshelfItem 테스트")
class BookshelfItemTest {

	private final User user = User.builder().userAuthority(UserAuthority.of(Role.USER)).build();
	private final Bookshelf bookshelf = Bookshelf.create("영지의 책장", user);
	private final Book book = BookObjectProvider.createRequiredFieldBook();
	private final BookshelfItemType type = BookshelfItemType.WISH;

	@DisplayName("create - BookshelfItem의 모든 필드가 유효 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			BookshelfItem.create(bookshelf, book, type);
		});
	}

	@DisplayName("create - type이 전달되지 않을 경우 default type으로 저장 - 성공")
	@Test
	void create_defaultType_success() {
		assertDoesNotThrow(() -> {
			BookshelfItem.create(bookshelf, book);
		});
	}

	@DisplayName("create - bookshelf가 null일 경우 - 실패")
	@Test
	void create_bookshelfNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookshelfItem.create(null, book, type);
		});
	}

	@DisplayName("create - book이 null일 경우 - 실패")
	@Test
	void create_bookNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookshelfItem.create(bookshelf, null, type);
		});
	}

	@DisplayName("create - type이 null일 경우 - 실패")
	@Test
	void create_typeNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookshelfItem.create(bookshelf, book, null);
		});
	}

	@DisplayName("create - bookshelf에 정상 추가 된다. - 성공")
	@Test
	void create_addBookshelf_success() {
		var bookshelfItem = BookshelfItem.create(bookshelf, book, type);
		assertTrue(bookshelf.getBookshelfItems().contains(bookshelfItem));
	}
}