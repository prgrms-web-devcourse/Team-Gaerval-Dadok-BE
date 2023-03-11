package com.dadok.gaerval.domain.book_group.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.exception.AlreadyContainBookGroupException;
import com.dadok.gaerval.domain.book_group.exception.ExceedLimitMemberException;
import com.dadok.gaerval.domain.book_group.exception.ExpiredJoinGroupPeriodException;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedPasswordException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.util.RegexHelper;
import com.dadok.gaerval.global.util.TimeHolder;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.GroupCommentObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

class BookGroupTest {

	private final Book book = BookObjectProvider.createRequiredFieldBook();

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private final TimeHolder timeHolder = TestTimeHolder.now();

	@DisplayName("create - bookGroup 생성 - 성공")
	@Test
	void create_success() {
		assertDoesNotThrow(() -> {

			BookGroup.create(2L, book, LocalDate.now(), LocalDate.now().plusDays(2),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - no limit member bookGroup 생성 - 성공")
	@Test
	void create_noLimit_success() {
		BookGroup bookGroup = BookGroup.create(2L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			null, "작은 모임", "작은 모임", true,
			"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);

		assertEquals(bookGroup.getMaxMemberCount(), 1000);
	}

	@DisplayName("create - maxCount가 1보다 작을경우 생성 - 실패")
	@Test
	void create_lessThen1_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			BookGroup.create(2L, book, LocalDate.now(), LocalDate.now().plusDays(2),
				0, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - userId null일 경우 - 실패")
	@Test
	void create_userIdNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", false, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - title null일 경우 - 실패")
	@Test
	void create_titleNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(),
				2, null, "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", false, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - title 30자 이상일 경우 - 실패")
	@Test
	void create_titleLengthOver30_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(null, book, LocalDate.now(), LocalDate.now(), 2,
				"작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임 작은 모임",
				"작은 모임", true, "월든 작가는?", "헨리데이빗소로우", true,
				passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - book null일 경우 - 실패")
	@Test
	void create_bookNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, null, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - StartDate null일 경우 - 실패")
	@Test
	void create_startDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, null, LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - endDate null일 경우 - 실패")
	@Test
	void create_endDateNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), null,
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", false, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - startDate 지난 날짜인 경우 - 실패")
	@Test
	void create_startDateValid_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().minusDays(1),
				LocalDate.now(), 2, "작은 모임", "작은 모임",
				true, "월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - endDate startDate보다 빠를 경우 - 실패")
	@Test
	void create_endDateValid_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now().plusDays(1),
				LocalDate.now().minusDays(1), 2, "작은 모임", "작은 모임",
				true, "월든 작가는?", "헨리데이빗소로우", true,
				passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - introduce null일 경우 - 실패")
	@Test
	void create_introduceNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", null, true,
				"월든 작가는?", "헨리데이빗소로우", false, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - isPublic null일 경우 - 실패")
	@Test
	void create_isPublicNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", "헨리데이빗소로우", null, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - joinPasswd null일 경우 - 실패")
	@Test
	void create_joinPasswdNull_fail() {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", null, true, passwordEncoder, timeHolder);
		});
	}

	@DisplayName("create - joinPasswd 공백을 포함하는 경우 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "하 이", "암 호 에 요", " 암호", "암호 "})
	void create_joinPasswdNull_fail(String password) {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", password, true, passwordEncoder, timeHolder);
		});
		assertTrue(RegexHelper.containsWhiteSpace(password, "password"));
	}

	@DisplayName("create - joinPasswd 암호 길이가 10자를 넘어가는 경우 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"12345678901", "rkskekfkakqktkdkwk", "가나다라마바사아자차탘차", "하나 둘 삼 넷 포병입니다", ""
		+ " 암호암호암호암호암호", "암호암호암호암호암호 "})
	void create_joinPasswdLengthOVer_fail(String password) {
		assertThrows(InvalidArgumentException.class, () -> {
			BookGroup.create(3L, book, LocalDate.now(), LocalDate.now(),
				2, "작은 모임", "작은 모임", true,
				"월든 작가는?", password, true, passwordEncoder, timeHolder);
		});
		assertTrue(password.length() > 10);
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

		//when
		GroupMember groupMember = GroupMember.create(bookGroup, kakaoUser, timeHolder);

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
			() -> bookGroup.addMember(groupMember, timeHolder));
	}

	@DisplayName("addMembers - maximum 정원 초과시 예외를 던진다.")
	@Test
	void addMembers_ExceedThrow() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			false, null, null, false, passwordEncoder, timeHolder
		);
		GroupMember.create(bookGroup, UserObjectProvider.createKakaoUser(), timeHolder);

		//when
		assertThrows(ExceedLimitMemberException.class,
			() -> GroupMember.create(bookGroup, UserObjectProvider.createNaverUser(), timeHolder));
	}

	@DisplayName("addMembers - 이미 참여한 사용자일 경우 예외를 던진다.")
	@Test
	void addMembers_AlreadyContainThrow() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		User user = UserObjectProvider.createKakaoUser();
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(book,
			1L);
		GroupMember.create(bookGroup, user, timeHolder);

		//when
		assertThrows(AlreadyContainBookGroupException.class,
			() -> GroupMember.create(bookGroup, user, timeHolder));
	}

	@DisplayName("addMembers - 모임의 시간이 지났다면 예외를 던진다.")
	@Test
	void addMembers_endDateOver_ExceedThrow() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(1),
			1, "책읽기 소모임", "책읽기 소모임",
			false, null, null, false, passwordEncoder, timeHolder
		);
		ReflectionTestUtils.setField(bookGroup, "startDate", LocalDate.now().minusDays(5));
		ReflectionTestUtils.setField(bookGroup, "endDate", LocalDate.now().minusDays(1));
		//when
		assertThrows(ExpiredJoinGroupPeriodException.class,
			() -> GroupMember.create(bookGroup, UserObjectProvider.createNaverUser(), timeHolder));
	}

	@DisplayName("addMembers - 모임의 모집 종료시간이 당일이여도 가입된다.")
	@Test
	void addMembers_endDateToday_ExceedThrow() {
		//given
		Book book = BookObjectProvider.createAllFieldBook();
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(1),
			1, "책읽기 소모임", "책읽기 소모임",
			false, null, null, false, passwordEncoder, timeHolder
		);
		//when
		assertDoesNotThrow(
			() -> GroupMember.create(bookGroup, UserObjectProvider.createNaverUser(), timeHolder));
	}

	@DisplayName("checkPasswd - 입력된 패스워드가 빈값이라면 예외를 던진다")
	@ParameterizedTest
	@EmptySource
	void checkPasswd_inputPasswdBlank_fail(String passwd) {
		//given
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			true, "숫자 일이삼사", "1234", false, passwordEncoder, timeHolder);
		//when
		assertThrows(NotMatchedPasswordException.class,
			() -> bookGroup.checkPasswd(passwd, passwordEncoder));
	}

	@DisplayName("checkPasswd - 입력된 패스워드가 null값이라면 예외를 던진다")
	@ParameterizedTest
	@NullSource
	void checkPasswd_inputPasswdNull_fail(String passwd) {
		//given
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			true, "숫자 일이삼사", "1234", false, passwordEncoder, timeHolder);
		//when
		assertThrows(IllegalArgumentException.class,
			() -> bookGroup.checkPasswd(passwd, passwordEncoder));
	}

	@DisplayName("checkPasswd - 입력된 패스워드가 틀리다면 예외를 던진다")
	@ParameterizedTest
	@ValueSource(strings = {"12345", "123", "12", "1", "123456", "가나다라", "1232", "1233", "1235", "4321", "라가23"})
	void checkPasswd_inputPasswdNotMatched_fail(String passwd) {
		//given
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			true, "숫자 일이삼사", "1234", false, passwordEncoder, timeHolder);
		//when
		assertThrows(NotMatchedPasswordException.class,
			() -> bookGroup.checkPasswd(passwd, passwordEncoder));
	}

	@DisplayName("checkPasswd - 패스워드가 설정되어 있지 않다면 통과한다.")
	@ParameterizedTest
	@ValueSource(strings = {"12345", "123", "12", "1", "123456", "가나다라", "1232", "1233", "1235", "4321", "라가23"})
	void checkPasswd_hasJoinPasswdFalse(String passwd) {
		//given
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			false, null, "1234", false, passwordEncoder, timeHolder);
		//when
		assertDoesNotThrow(() -> bookGroup.checkPasswd(passwd, passwordEncoder));
	}

	@DisplayName("checkPasswd - 패스워드가 일치하다면 통과한다.")
	@Test
	void checkPasswd_matched_success() {
		//given
		BookGroup bookGroup = BookGroup.create(
			1L, book, LocalDate.now(), LocalDate.now().plusDays(2),
			1, "책읽기 소모임", "책읽기 소모임",
			false, null, "1234", false, passwordEncoder, timeHolder);
		String password = "1234";
		//when
		assertDoesNotThrow(() -> bookGroup.checkPasswd(password, passwordEncoder));
	}

}