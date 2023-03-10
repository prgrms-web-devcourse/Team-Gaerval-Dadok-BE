package com.dadok.gaerval.domain.book_group.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.util.TimeHolder;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

class GroupMemberTest {

	private final Book book = BookObjectProvider.createRequiredFieldBook();
	private final BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 3L);
	private final User user = UserObjectProvider.createKakaoUser();
	private final TimeHolder timeHolder = TestTimeHolder.now();
	@DisplayName("create - GroupMember의 필드가 유효 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			GroupMember.create(bookGroup, user, timeHolder);
		});
	}

	@DisplayName("create - user가 null 일 경우 - 실패")
	@Test
	void create_userNull_fail() {
		assertThrows(InvalidArgumentException.class, () ->
			GroupMember.create(bookGroup, null, timeHolder));
	}

	@DisplayName("create - book group이 null 일 경우 - 실패")
	@Test
	void create_bookGroupNull_fail() {
		assertThrows(InvalidArgumentException.class, () ->
			GroupMember.create(null, user, timeHolder));
	}

	@DisplayName("joinGroup - Group에 가입한다.- 성공")
	@Test
	void joinGroup_success() {
		//when
		GroupMember groupMember = GroupMember.create(bookGroup, user, timeHolder);
		//then
		assertEquals(groupMember.getBookGroup(), bookGroup);
		assertTrue(bookGroup.getGroupMembers().contains(groupMember));
	}


}