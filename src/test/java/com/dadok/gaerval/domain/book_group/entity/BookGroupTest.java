package com.dadok.gaerval.domain.book_group.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.GroupCommentObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class BookGroupTest {

	private final Book book = BookObjectProvider.createRequiredFieldBook();

	@DisplayName("create - bookGroup 생성 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {
			BookGroup.create(2L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - maxCount가 1보다 작을경우 생성 - 실패")
	@Test
	void create_lessThen1_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookGroup.create(2L, book, LocalDate.now(), LocalDate.now(),
				0, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - userId null일 경우 - 실패")
	@Test
	void create_userIdNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - title null일 경우 - 실패")
	@Test
	void create_titleNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(),
				2, null, "작은 모임", true);
		});
	}

	@DisplayName("create - title 30자 이상일 경우 - 실패")
	@Test
	void create_titleLengthOver30_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(), 2,
				"작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임",
				"작은 모임", true);
		});
	}

	@DisplayName("create - book null일 경우 - 실패")
	@Test
	void create_bookNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, null, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - StartDate null일 경우 - 실패")
	@Test
	void create_startDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, null, LocalDate.now(),
				2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - endDate null일 경우 - 실패")
	@Test
	void create_endDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), null,
				2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - startDate 지난 날짜인 경우 - 실패")
	@Test
	void create_startDateValid_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().minusDays(1),
				LocalDate.now(), 2, "작은 모임", "작은 모임", true);
		});
	}

	@DisplayName("create - endDate startDate보다 빠를 경우 - 실패")
	@Test
	void create_endDateValid_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().plusDays(1),
				LocalDate.now().minusDays(1), 2, "작은 모임", "작은 모임",
				true);
		});
	}

	@DisplayName("create - introduce null일 경우 - 실패")
	@Test
	void create_introduceNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", null, true);
		});
	}

	@DisplayName("create - isPublic null일 경우 - 실패")
	@Test
	void create_isPublicNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", null);
		});
	}

	@DisplayName("addComments - GroupComment가 null이 아니면 comments에 add 한다.")
	@Test
	void addComments() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book,
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();

		GroupComment groupComment = GroupCommentObjectProvider.createComment("contents", bookGroup, kakaoUser);
		//when
		bookGroup.addComment(groupComment);

		//then
		assertEquals(1, bookGroup.getComments().size());
		assertEquals(1L, bookGroup.getOwnerId());
		assertEquals(book, bookGroup.getBook());
		assertTrue(bookGroup.getComments().contains(groupComment));
		assertEquals(groupComment.getBookGroup(), bookGroup);
	}

	@DisplayName("addComments - GroupComment가 null이 면 예외를 던진다.")
	@Test
	void addComments_throw() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book,
			1L);
		GroupComment groupComment = null;
		//when
		assertThrows(InvalidArgumentException.class,
			() -> bookGroup.addComment(groupComment));
	}

	@DisplayName("addMembers - GroupMember가 null이 아니면 members에 add 한다.")
	@Test
	void addMembers() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book,
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();

		GroupMember groupMember = GroupMember.create(bookGroup, kakaoUser);

		//when
		bookGroup.addMember(groupMember);

		//then
		assertEquals(1, bookGroup.getGroupMembers().size());
		assertEquals(1L, bookGroup.getOwnerId());
		assertTrue(bookGroup.getGroupMembers().contains(groupMember));
		assertEquals(bookGroup.getGroupMembers().get(0).getUser(), kakaoUser);
		assertEquals(groupMember.getBookGroup(), bookGroup);
	}

	@DisplayName("addMembers - GroupMember가 null이 면 예외를 던진다.")
	@Test
	void addMembers_throw() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book,
			1L);
		GroupMember groupMember = null;
		//when
		assertThrows(InvalidArgumentException.class,
			() -> bookGroup.addMember(groupMember));
	}

}