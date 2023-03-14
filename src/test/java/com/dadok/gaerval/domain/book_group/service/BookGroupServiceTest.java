package com.dadok.gaerval.domain.book_group.service;

import static com.dadok.gaerval.domain.book_group.entity.BookGroup.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.exception.ExceedLimitMemberException;
import com.dadok.gaerval.domain.book_group.exception.ExpiredJoinGroupPeriodException;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.integration_util.IntegrationTest;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.extern.slf4j.Slf4j;

@Tag("Integration Test")
@Sql(scripts = {"/sql/job_data.sql", "/sql/authority_data.sql"})
@Sql(scripts = {"/sql/clean_up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Slf4j
@Transactional
class BookGroupServiceTest extends IntegrationTest {

	@Autowired
	private DefaultBookGroupService defaultBookGroupService;

	private Book book;

	private User owner;

	private Bookshelf bookshelf;

	@BeforeEach
	void setUp() {
		Book book = BookObjectProvider.createBook();
		this.book = bookRepository.save(book);
		User owner = User.createByOAuth(UserObjectProvider.naverAttribute(),
			UserAuthority.create(getAuthority(Role.USER)));
		ReflectionTestUtils.setField(owner, "email", "testEmail1234@naver.com");
		this.owner = userRepository.save(owner);
		Bookshelf bookshelf = Bookshelf.create(owner);
		this.bookshelf = bookshelfRepository.save(bookshelf);
	}

	@Transactional
	@DisplayName("모임을 생성할 수 있다.")
	@Test
	void create_success() {
		//given
		BookGroupCreateRequest bookGroupCreateRequest =
			new BookGroupCreateRequest(book.getId(), "영지네", LocalDate.now(), LocalDate.now().plusDays(7), 5, "영지랑 놀아요",
				false, null, null, true);

		User requestUser = saveUser("yougnji804@naver.com");

		//when
		Long bookGroupId = defaultBookGroupService.createBookGroup(requestUser.getId(), bookGroupCreateRequest);

		//then
		Optional<BookGroup> findBookGroup = bookGroupRepository.findById(bookGroupId);

		assertThat(findBookGroup.isPresent()).isTrue();
		assertThat(findBookGroup.get().getTitle()).isEqualTo("영지네");
		assertThat(findBookGroup.get().getMaxMemberCount()).isEqualTo(5);
		assertThat(findBookGroup.get().getIntroduce()).isEqualTo("영지랑 놀아요");
	}

	@Transactional
	@DisplayName("인원 제한 없음 모임을 생성할 수 있다.")
	@Test
	void create_noLimit_success() {
		//given
		BookGroupCreateRequest bookGroupCreateRequest =
			new BookGroupCreateRequest(book.getId(), "영지네", LocalDate.now(), LocalDate.now().plusDays(7), null,
				"영지랑 놀아요",
				false, null, null, true);

		User requestUser = saveUser("yougnji804@naver.com");

		//when
		Long bookGroupId = defaultBookGroupService.createBookGroup(requestUser.getId(), bookGroupCreateRequest);

		//then
		Optional<BookGroup> findBookGroup = bookGroupRepository.findById(bookGroupId);

		assertThat(findBookGroup.isPresent()).isTrue();
		assertThat(findBookGroup.get().getTitle()).isEqualTo("영지네");
		assertThat(findBookGroup.get().getMaxMemberCount()).isEqualTo(NO_LIMIT_MEMBER_COUNT);
		assertThat(findBookGroup.get().getIntroduce()).isEqualTo("영지랑 놀아요");
	}

	@Transactional
	@DisplayName("create - 잠금 모임 생성시 비밀번호, 질문 없을 경우 - 실패")
	@Test
	void create_noJoinQnA_fail() {
		//given
		BookGroupCreateRequest bookGroupCreateRequest =
			new BookGroupCreateRequest(book.getId(), "영지네", LocalDate.now(), LocalDate.now().plusDays(7), null,
				"영지랑 놀아요",
				true, null, null, true);

		User requestUser = saveUser("yougnji804@naver.com");

		//when // then
		assertThrows(InvalidArgumentException.class, () -> {
			defaultBookGroupService.createBookGroup(requestUser.getId(), bookGroupCreateRequest);
		});
	}

	@Transactional
	@DisplayName("인원 제한 없음 모임을 조회할 수 있다.")
	@Test
	void findGroup_success() {
		//given
		BookGroupCreateRequest bookGroupCreateRequest =
			new BookGroupCreateRequest(book.getId(), "영지네", LocalDate.now(), LocalDate.now().plusDays(7), null,
				"영지랑 놀아요",
				false, null, null, true);

		User requestUser = saveUser("yougnji804@naver.com");
		Long bookGroupId = defaultBookGroupService.createBookGroup(requestUser.getId(), bookGroupCreateRequest);

		//when
		BookGroupDetailResponse bookGroupDetailResponse =
			defaultBookGroupService.findGroup(requestUser.getId(), bookGroupId);

		//then
		assertThat(bookGroupDetailResponse.title()).isEqualTo("영지네");
		assertThat(bookGroupDetailResponse.maxMemberCount()).isNull();
		assertThat(bookGroupDetailResponse.isGroupMember()).isTrue();
		assertThat(bookGroupDetailResponse.currentMemberCount()).isEqualTo(1);
	}

	@Transactional
	@DisplayName("모임에 참여할 수 있다.")
	@Test
	void join_success() {
		//given
		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now(), LocalDate.now().plusDays(7),
			5, "인원제한이 5명인 그룹", "인원제한이 5명인 그룹",
			false, null, null, true,
			new BCryptPasswordEncoder(), TestTimeHolder.now());
		bookGroup.addMember(GroupMember.create(owner), TestTimeHolder.now());
		bookGroupRepository.save(bookGroup);

		User requestUser = saveUser("ysking@naver.com");

		BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest(null);
		//when
		defaultBookGroupService.join(bookGroup.getId(), requestUser.getId(), bookGroupJoinRequest);

		//then
		Optional<BookGroup> findBookGroup = bookGroupRepository.findById(bookGroup.getId());

		assertThat(findBookGroup.isPresent()).isTrue();
		int memberCount = findBookGroup.get().getGroupMembers().size();
		assertEquals(2, memberCount);
		boolean exists = groupMemberRepository.existsByBookGroupIdAndUserId(findBookGroup.get().getId(),
			requestUser.getId());
		assertTrue(exists);
	}

	@Transactional
	@DisplayName("모임에 참여할 수 있다. - 비밀번호 ")
	@Test
	void join_passwd_success() {
		//given
		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now(), LocalDate.now().plusDays(7),
			5, "인원제한이 5명인 그룹", "인원제한이 5명인 그룹",
			true, "질문", "답변", true,
			new BCryptPasswordEncoder(), TestTimeHolder.now());
		bookGroup.addMember(GroupMember.create(owner), TestTimeHolder.now());
		bookGroupRepository.save(bookGroup);

		User requestUser = saveUser("ysking@naver.com");

		BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest("답변");
		//when
		defaultBookGroupService.join(bookGroup.getId(), requestUser.getId(), bookGroupJoinRequest);

		//then
		Optional<BookGroup> findBookGroup = bookGroupRepository.findById(bookGroup.getId());

		assertThat(findBookGroup.isPresent()).isTrue();
		int memberCount = findBookGroup.get().getGroupMembers().size();
		assertEquals(2, memberCount);
		boolean exists = groupMemberRepository.existsByBookGroupIdAndUserId(findBookGroup.get().getId(),
			requestUser.getId());
		assertTrue(exists);
	}

	@Transactional
	@DisplayName("인원이 가득 찬 모임에 참여할 수 없다.")
	@Test
	void cant_join_success() {
		//given
		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now(), LocalDate.now().plusDays(7),
			1, "인원제한이 1명인 그룹", "인원제한이 1명인 그룹",
			false, null, null, true,
			new BCryptPasswordEncoder(), TestTimeHolder.now());
		bookGroup.addMember(GroupMember.create(owner), TestTimeHolder.now());
		bookGroupRepository.save(bookGroup);

		User requestUser = saveUser("ysking@naver.com");

		BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest(null);
		//when
		assertThrows(ExceedLimitMemberException.class, () ->
			defaultBookGroupService.join(bookGroup.getId(), requestUser.getId(), bookGroupJoinRequest)
		);

		//then
		assertEquals(bookGroup.getGroupMembers().size(), bookGroup.getMaxMemberCount());
	}

	@Transactional
	@DisplayName("인원이 가득 찬 모임에 참여할 수 없다.")
	@Test
	void cant_join_success2() {
		//given
		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now(), LocalDate.now().plusDays(7),
			2, "인원제한이 2명인 그룹", "인원제한이 2명인 그룹",
			false, null, null, true,
			new BCryptPasswordEncoder(), TestTimeHolder.now());
		bookGroup.addMember(GroupMember.create(owner), TestTimeHolder.now());
		bookGroupRepository.save(bookGroup);

		User otherUser = saveUser("ysking@naver.com");
		bookGroup.addMember(GroupMember.create(otherUser), TestTimeHolder.now());

		User requestUser = saveUser("ysking2@naver.com");

		BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest(null);
		//when
		assertThrows(ExceedLimitMemberException.class, () ->
			defaultBookGroupService.join(bookGroup.getId(), requestUser.getId(), bookGroupJoinRequest)
		);

		//then
		assertEquals(bookGroup.getGroupMembers().size(), bookGroup.getMaxMemberCount());
	}

	@Transactional
	@DisplayName("가입기간이 지난 모임에 참여할 수 없다.")
	@Test
	void cant_join_expiration() {
		//given
		BookGroup bookGroup = BookGroup.create(owner.getId(), book, LocalDate.now().minusDays(7),
			LocalDate.now().minusDays(1),
			2, "인원제한이 2명인 그룹", "인원제한이 2명인 그룹",
			false, null, null, true,
			new BCryptPasswordEncoder(),
			new TestTimeHolder(TestTimeHolder.localDateToClockStartOfDay(LocalDate.now().minusDays(8))));
		bookGroup.addMember(GroupMember.create(owner), TestTimeHolder.of(LocalDate.now().minusDays(5)));
		bookGroupRepository.save(bookGroup);

		User requestUser = saveUser("ysking2@naver.com");

		BookGroupJoinRequest bookGroupJoinRequest = new BookGroupJoinRequest(null);
		//when
		assertThrows(ExpiredJoinGroupPeriodException.class, () ->
			defaultBookGroupService.join(bookGroup.getId(), requestUser.getId(), bookGroupJoinRequest)
		);

		//then
		assertEquals(bookGroup.getGroupMembers().size(), 1);
	}

	private User saveUser(String email) {
		User requestUser = User.createByOAuth(UserObjectProvider.naverAttribute(),
			UserAuthority.create(getAuthority(Role.USER)));
		ReflectionTestUtils.setField(requestUser, "email", email);
		userRepository.save(requestUser);
		Bookshelf requestUserBookshelf = Bookshelf.create(requestUser);
		bookshelfRepository.save(requestUserBookshelf);
		return requestUser;
	}
}