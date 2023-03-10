package com.dadok.gaerval.domain.book_group.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.exception.AlreadyContainBookGroupException;
import com.dadok.gaerval.domain.book_group.exception.BookGroupOwnerNotMatchedException;
import com.dadok.gaerval.domain.book_group.exception.CannotDeleteMemberExistException;
import com.dadok.gaerval.domain.book_group.exception.LessThanCurrentMembersException;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedPasswordException;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.book_group.repository.GroupMemberRepository;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.global.util.TimeHolder;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookGroupServiceSliceTest {

	@InjectMocks
	private DefaultBookGroupService defaultBookGroupService;

	@Mock
	private BookGroupRepository bookGroupRepository;

	@Mock
	private GroupMemberRepository groupMemberRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserService userService;

	@Mock
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private BookshelfService bookshelfService;

	private TimeHolder timeHolder = TestTimeHolder.now();

	@BeforeEach
	private void setUp() {
		defaultBookGroupService =
			new DefaultBookGroupService(bookGroupRepository, groupMemberRepository, bookService,
				userService, passwordEncoder, bookshelfService, timeHolder);
	}

	@DisplayName("findAllBookGroups - 모임 리스트를 조회한다.")
	@Test
	void findAllBookGroups() {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);

		List<BookGroupResponse> responses = BookGroupObjectProvider.mockBookGroupResponses();

		BookGroupResponses bookGroupResponses = new BookGroupResponses(
			QueryDslUtil.toSlice(responses, PageRequest.of(0, 10)));

		given(bookGroupRepository.findAllBy(request))
			.willReturn(bookGroupResponses);
		//when
		BookGroupResponses bookGroups = defaultBookGroupService.findAllBookGroups(request);
		//then
		assertThat(bookGroups)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", 5)
			.hasFieldOrPropertyWithValue("isEmpty", false);

		assertThat(bookGroups.bookGroups()).isSortedAccordingTo(
			(o1, o2) -> o2.getBookGroupId().compareTo(o1.getBookGroupId()));

	}

	@DisplayName("findAllBookGroupsByUser - 나의 모임 리스트를 조회한다.")
	@Test
	void findAllBookGroupsByUser() {
		//given
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);

		List<BookGroupResponse> responses = BookGroupObjectProvider.mockBookGroupResponses();

		BookGroupResponses bookGroupResponses = new BookGroupResponses(
			QueryDslUtil.toSlice(responses, PageRequest.of(0, 10)));

		given(bookGroupRepository.findAllByUser(request, 1L))
			.willReturn(bookGroupResponses);

		//when
		BookGroupResponses bookGroups = defaultBookGroupService.findAllBookGroupsByUser(request, 1L);

		//then
		assertThat(bookGroups)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", 5)
			.hasFieldOrPropertyWithValue("isEmpty", false);

		assertThat(bookGroups.bookGroups()).isSortedAccordingTo(
			(o1, o2) -> o2.getBookGroupId().compareTo(o1.getBookGroupId()));

	}

	@DisplayName("createBookGroup - 모임 생성에 성공한다.")
	@Test
	void createBookGroup_success() {
		// Given
		var user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		var book = BookObjectProvider.createRequiredFieldBook();
		var request = new BookGroupCreateRequest(book.getId(),
			"소모임 화이팅", LocalDate.now(), LocalDate.now().plusDays(2), 5, "우리끼리 옹기종기", true, "월든 작가는?", "헨리데이빗소로우", false
		);
		BookGroup bookGroup = BookGroup.create(user.getId(), book, request.startDate(), request.endDate(),
			request.maxMemberCount(), request.title(), request.introduce(), request.hasJoinPasswd(),
			request.joinQuestion(), request.joinPasswd(), request.isPublic(), new BCryptPasswordEncoder(), timeHolder);
		ReflectionTestUtils.setField(bookGroup, "id", 1L);

		given(userService.getById(user.getId()))
			.willReturn(user);
		given(bookService.findById(request.bookId()))
			.willReturn(Optional.of(book));
		given(bookGroupRepository.save(any()))
			.willReturn(bookGroup);

		// When
		var bookGroupId = defaultBookGroupService.createBookGroup(user.getId(), request);

		// Then
		verify(bookService).findById(request.bookId());
		verify(bookGroupRepository).save(any());
		assertThat(bookGroupId).isEqualTo(1L);
	}

	@DisplayName("findGroup - 없는 그룹이므로 예외를 던진다.")
	@Test
	void findGroup_throw() {
		//given
		given(bookGroupRepository.findBookGroup(2L, 100L))
			.willThrow(new ResourceNotfoundException(BookGroup.class));
		//when
		assertThrows(ResourceNotfoundException.class,
			() -> defaultBookGroupService.findGroup(2L, 100L));
	}

	@DisplayName("findGroup - 그룹을 조회한다.")
	@Test
	void findGroup_Success() {
		//given
		long bookGroupId = 999L;
		String title = "김영한님 JPA 읽으실분";
		String introduce = "우리모임은 JPA 책 스터디를 하려고 모여있어요";
		long ownerId = 1L;
		long bookId = 10L;
		boolean isOwner = false;
		boolean isGroupMember = true;
		LocalDate startDate = LocalDate.now().plusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(7);
		String bookTitle = "Java ORM 표준 JPA 프로그래밍";
		String bookImageUrl = "http://jpaimage.jpeg";
		int maxMemberCount = 5;
		long currentMemberCount = 2L;
		long commentCount = 5L;
		BookGroupDetailResponse.BookResponse book = new BookGroupDetailResponse.BookResponse(bookId, bookImageUrl,
			bookTitle);

		BookGroupDetailResponse.OwnerResponse owner = new BookGroupDetailResponse.OwnerResponse(ownerId);

		BookGroupDetailResponse bookGroupDetailResponse = new BookGroupDetailResponse(bookGroupId,
			title, introduce,
			startDate, endDate, false, "인생책은?", true, maxMemberCount, currentMemberCount, commentCount,
			owner,
			book,
			isOwner, isGroupMember
		);
		//when
		given(bookGroupRepository.findBookGroup(2L, bookGroupId))
			.willReturn(bookGroupDetailResponse);

		//then
		BookGroupDetailResponse response = defaultBookGroupService.findGroup(2L, bookGroupId);

		assertThat(response)
			.hasFieldOrPropertyWithValue("bookGroupId", bookGroupId)
			.hasFieldOrPropertyWithValue("title", title)
			.hasFieldOrPropertyWithValue("introduce", introduce)
			.hasFieldOrPropertyWithValue("isOwner", isOwner)
			.hasFieldOrPropertyWithValue("isGroupMember", isGroupMember)
			.hasFieldOrPropertyWithValue("startDate", startDate)
			.hasFieldOrPropertyWithValue("endDate", endDate)
			.hasFieldOrPropertyWithValue("maxMemberCount", maxMemberCount)
			.hasFieldOrPropertyWithValue("currentMemberCount", currentMemberCount)
			.hasFieldOrPropertyWithValue("commentCount", commentCount);

		assertThat(response.owner()).isEqualTo(owner);
		assertThat(response.book()).isEqualTo(book);
	}

	@DisplayName("findById - 북그룹을 반환한다.")
	@Test
	void findById_success() {
		//given
		Book book = BookObjectProvider.createBook();
		BookGroup bookGroup = BookGroup.create(1L,
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", true,
			"월든 작가는?", "헨리데이빗소로우", true, passwordEncoder, timeHolder
		);
		given(bookGroupRepository.findById(1L))
			.willReturn(Optional.of(bookGroup));
		//when
		Optional<BookGroup> bookGroupOptional = defaultBookGroupService.findById(1L);
		//then
		assertEquals(bookGroupOptional.get(), bookGroup);
	}

	@DisplayName("join - 이미 북 그룹에 가입된 멤버라면 가입에 실패한다.")
	@Test
	void join_fail() {
		//given
		Long userId = 100L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "id", userId);

		Book book = BookObjectProvider.createBook();
		BookGroup bookGroup = BookGroup.create(1L,
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", false,
			null, null, true, passwordEncoder, timeHolder
		);
		long groupId = 99L;
		ReflectionTestUtils.setField(bookGroup, "id", groupId);

		GroupMember groupMember = GroupMember.create(kakaoUser);
		bookGroup.addMember(groupMember, timeHolder);

		given(bookGroupRepository.findByIdWithGroupMembersForUpdate(groupId))
			.willReturn(Optional.of(bookGroup));

		given(userService.getById(userId))
			.willReturn(kakaoUser);
		//when
		assertThrows(AlreadyContainBookGroupException.class,
			() -> defaultBookGroupService.join(bookGroup.getId(), userId, new BookGroupJoinRequest(null)));

		//then
		assertTrue(bookGroup.getGroupMembers().contains(groupMember));
		verify(bookGroupRepository).findByIdWithGroupMembersForUpdate(groupId);
		verify(userService).getById(userId);
	}

	@DisplayName("join - 비밀번호가 틀리다면 가입에 실패한다.")
	@Test
	void join_notMatchedPassword_fail() {
		//given
		Long userId = 100L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "id", userId);

		Book book = BookObjectProvider.createBook();
		BookGroup bookGroup = BookGroup.create(1L,
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", true,
			"일이삼사", "1234", true, passwordEncoder, timeHolder
		);
		long groupId = 99L;
		ReflectionTestUtils.setField(bookGroup, "id", groupId);

		given(bookGroupRepository.findByIdWithGroupMembersForUpdate(groupId))
			.willReturn(Optional.of(bookGroup));

		String passwd = "12345";

		//when
		assertThrows(NotMatchedPasswordException.class,
			() -> defaultBookGroupService.join(bookGroup.getId(), userId, new BookGroupJoinRequest(passwd)));

		//then
		verify(bookGroupRepository).findByIdWithGroupMembersForUpdate(groupId);
	}

	@DisplayName("deleteBookGroup - 그룹을 삭제한다.")
	@Test
	void deleteBookGroup_Success() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));
		doNothing().when(bookGroupRepository).deleteById(2L);

		//when
		defaultBookGroupService.deleteBookGroup(2L, 1L);

		//then
		verify(bookGroupRepository).findById(2L);
		verify(bookGroupRepository).deleteById(2L);
	}

	@DisplayName("deleteBookGroup - 그룹이 존재하지 않을 경우 - 실패")
	@Test
	void deleteBookGroup_bookGroupNotExist_fail() {
		//given
		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.empty());

		//when
		assertThrows(ResourceNotfoundException.class, () ->
			defaultBookGroupService.deleteBookGroup(2L, 1L)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("deleteBookGroup - 사용자가 모임장이 아닐경우 - 실패")
	@Test
	void deleteBookGroup_notOwner_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(BookGroupOwnerNotMatchedException.class, () ->
			defaultBookGroupService.deleteBookGroup(2L, 2L)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("deleteBookGroup - 모임원이 존재할 경우 - 실패")
	@Test
	void deleteBookGroup_existMember_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var naverUser = UserObjectProvider.createNaverUser();
		var kakaoUser = UserObjectProvider.createNaverUser();
		ReflectionTestUtils.setField(naverUser, "id", 1L);
		ReflectionTestUtils.setField(kakaoUser, "id", 2L);

		GroupMember.create(bookGroup, kakaoUser, timeHolder);
		GroupMember.create(bookGroup, naverUser, timeHolder);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(CannotDeleteMemberExistException.class, () ->
			defaultBookGroupService.deleteBookGroup(2L, 1L)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("updateBookGroup - 그룹을 수정한다.")
	@Test
	void updateBookGroup_Success() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().plusDays(3), 5
		);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertDoesNotThrow(() ->
			defaultBookGroupService.updateBookGroup(2L, 1L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
		assertThat(bookGroup.getIntroduce()).isEqualTo("변경된 내용");
		assertThat(bookGroup.getEndDate()).isEqualTo(LocalDate.now().plusDays(3));
		assertThat(bookGroup.getMaxMemberCount()).isEqualTo(5);
	}

	@DisplayName("updateBookGroup - 그룹이 존재하지 않을 경우 - 실패")
	@Test
	void updateBookGroup_bookGroupNotExist_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().plusDays(3), 5
		);
		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.empty());

		//when
		assertThrows(ResourceNotfoundException.class, () ->
			defaultBookGroupService.updateBookGroup(2L, 1L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("updateBookGroup - 사용자가 모임장이 아닐경우 - 실패")
	@Test
	void updateBookGroup_notOwner_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().plusDays(3), 5
		);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(BookGroupOwnerNotMatchedException.class, () ->
			defaultBookGroupService.updateBookGroup(2L, 2L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("updateBookGroup - endDate가 과거일 경우")
	@Test
	void updateBookGroup_EndDateToday_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().minusDays(1), 5
		);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(InvalidArgumentException.class, () ->
			defaultBookGroupService.updateBookGroup(2L, 1L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("updateBookGroup - endDate가 startDate 보다 빠를 경우")
	@Test
	void updateBookGroup_validateEndDateLessStartDate_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);
		ReflectionTestUtils.setField(bookGroup, "startDate", LocalDate.now().plusDays(10));
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().plusDays(5), 5
		);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(InvalidArgumentException.class, () ->
			defaultBookGroupService.updateBookGroup(2L, 1L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("updateBookGroup - 수정 최대인원이 현재 인원보다 작을 경우")
	@Test
	void updateBookGroup_lessThanCurrentMember_fail() {
		//given
		var book = BookObjectProvider.createBook();
		var bookGroup = BookGroupObjectProvider.createMockBookGroup(book, 1L);

		var kakaoUser = UserObjectProvider.createKakaoUser();
		var naverUser = UserObjectProvider.createNaverUser();
		ReflectionTestUtils.setField(kakaoUser, "id", 1L);
		ReflectionTestUtils.setField(naverUser, "id", 2L);
		GroupMember.create(bookGroup, naverUser, timeHolder);
		GroupMember.create(bookGroup, kakaoUser, timeHolder);
		var request = new BookGroupUpdateRequest(
			bookGroup.getTitle(), "변경된 내용", LocalDate.now().plusDays(3), 1
		);

		given(bookGroupRepository.findById(2L))
			.willReturn(Optional.of(bookGroup));

		//when
		assertThrows(LessThanCurrentMembersException.class, () ->
			defaultBookGroupService.updateBookGroup(2L, 1L, request)
		);

		//then
		verify(bookGroupRepository).findById(2L);
	}

	@DisplayName("getById - bookGroupId로 조회에 성공한다.")
	@Test
	void getById() {
		// given
		Long userId = 1L;
		Long bookGroupId = 234L;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			userId);
		given(bookGroupRepository.findById(bookGroupId))
			.willReturn(Optional.of(bookGroup));

		// when
		BookGroup findBookGroup = defaultBookGroupService.getById(bookGroupId);

		// then
		verify(bookGroupRepository).findById(bookGroupId);
		assertEquals(bookGroup, findBookGroup);
	}

	@DisplayName("findById - bookGroupId로 조회에 성공한다.")
	@Test
	void findById() {
		// given
		Long userId = 1L;
		Long bookGroupId = 234L;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			userId);
		given(bookGroupRepository.findById(bookGroupId))
			.willReturn(Optional.of(bookGroup));

		// when
		Optional<BookGroup> findBookGroup = defaultBookGroupService.findById(bookGroupId);

		// then
		verify(bookGroupRepository).findById(bookGroupId);
		assertTrue(findBookGroup.isPresent());
		assertEquals(bookGroup, findBookGroup.get());
	}

}