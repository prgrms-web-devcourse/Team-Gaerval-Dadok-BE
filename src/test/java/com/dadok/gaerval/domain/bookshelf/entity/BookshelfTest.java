package com.dadok.gaerval.domain.bookshelf.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@DisplayName("Bookshelf 테스트")
class BookshelfTest {

	private final User user = UserObjectProvider.createKakaoUser();

	@DisplayName("create - Bookshelf의 필드가 유효 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			Bookshelf.create(user);
		});
	}

	@DisplayName("changeName - 성공")
	@Test
	void changeName_success() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertDoesNotThrow(() -> {
			bookshelf.changeName("개발자의 책장");
		});
	}

	@DisplayName("changeIsPublic - 성공")
	@Test
	void changeIsPublic_success() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertDoesNotThrow(() -> {
			bookshelf.changeIsPublic(false);
		});
	}

	@DisplayName("changeName - name이 null일 경우 - 실패")
	@Test
	void changeName_nameNull_fail() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertThrows(IllegalArgumentException.class, () -> bookshelf.changeName(null));
	}

	@DisplayName("changeIsPublic - isPublic이 null일 경우 - 실패")
	@Test
	void changeIsPublic_isPublicNull_fail() {
		Bookshelf bookshelf = Bookshelf.create(user);
		assertThrows(IllegalArgumentException.class, () -> bookshelf.changeIsPublic(null));
	}

	@DisplayName("create - user가 null일 경우 - 실패")
	@Test
	void create_UserNull_fail() {
		assertThrows(IllegalArgumentException.class, () -> Bookshelf.create(null));
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
		assertThrows(IllegalArgumentException.class, () -> bookshelf.addBookShelfItem(null));
	}

}