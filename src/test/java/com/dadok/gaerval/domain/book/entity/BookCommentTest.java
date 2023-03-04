package com.dadok.gaerval.domain.book.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class BookCommentTest {

	@DisplayName("BookComment 엔티티를 생성하는데 성공한다.")
	@Test
	void create_bookComment_success() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		Book book = BookObjectProvider.createRequiredFieldBook();
		String comment = "아들러 사상이 담긴 책이라고 해서 봤는데 대화로 이루어지는 전개가 흥미로웠어요.";

		// when
		BookComment bookComment = BookComment.create(user, book, comment);

		// then
		assertEquals(user, bookComment.getUser());
		assertEquals(book, bookComment.getBook());
		assertEquals(comment, bookComment.getComment());
	}

	@DisplayName("comment를 수정하는데 성공한다.")
	@Test
	void changeComment_success() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		Book book = BookObjectProvider.createRequiredFieldBook();
		String comment = "아들러 사상이 담긴 책이라고 해서 봤는데 대화형식이 흥미로웠어요.";

		BookComment bookComment = BookComment.create(user, book, comment);

		// when
		comment = "흥미롭기는 했는데 적용하기에는 너무 추상적이었어요.";
		bookComment.changeComment(comment);

		//then
		assertEquals(comment, bookComment.getComment());
	}
}