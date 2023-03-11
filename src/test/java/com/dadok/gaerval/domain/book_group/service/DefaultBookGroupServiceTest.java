package com.dadok.gaerval.domain.book_group.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.exception.ExceedLimitMemberException;
import com.dadok.gaerval.domain.book_group.exception.ExpiredJoinGroupPeriodException;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.integration_util.ServiceIntegration;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.extern.slf4j.Slf4j;

@Sql(scripts = {"/sql/job_data.sql", "/sql/authority_data.sql"})
@Sql(scripts = {"/sql/clean_up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Slf4j
@Transactional
class DefaultBookGroupServiceTest extends ServiceIntegration {

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
		BookGroup findBookGroup = bookGroupRepository.getReferenceById(bookGroup.getId());

		int memberCount = findBookGroup.getGroupMembers().size();
		assertEquals(2, memberCount);
		boolean exists = groupMemberRepository.existsByBookGroupIdAndUserId(findBookGroup.getId(), requestUser.getId());
		assertTrue(exists);
	}

	@Transactional
	@DisplayName("모임에 참여할 수 있다.")
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
		BookGroup findBookGroup = bookGroupRepository.getReferenceById(bookGroup.getId());

		int memberCount = findBookGroup.getGroupMembers().size();
		assertEquals(2, memberCount);
		boolean exists = groupMemberRepository.existsByBookGroupIdAndUserId(findBookGroup.getId(), requestUser.getId());
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