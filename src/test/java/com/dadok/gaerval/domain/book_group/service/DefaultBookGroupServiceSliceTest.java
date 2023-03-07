package com.dadok.gaerval.domain.book_group.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.exception.BookGroupOwnerNotMatchedException;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookGroupServiceSliceTest {

	@InjectMocks
	private DefaultBookGroupService defaultBookGroupService;

	@Mock
	private BookGroupRepository bookGroupRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserService userService;

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
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());
		BookGroupCreateRequest request = new BookGroupCreateRequest(bookCreateRequest,
			"소모임 화이팅", LocalDate.now(), LocalDate.now(), 5, "우리끼리 옹기종기", true, "월든 작가는?", "헨리데이빗소로우", false
		);
		BookGroup bookGroup = BookGroup.create(user.getId(), book, request.startDate(), request.endDate(),
			request.maxMemberCount(), request.title(), request.introduce(), request.hasJoinPasswd(),
			request.joinQuestion(), request.joinPasswd(), request.isPublic());
		ReflectionTestUtils.setField(bookGroup, "id", 1L);

		given(userService.getById(user.getId()))
			.willReturn(user);
		given(bookService.createBook(bookCreateRequest))
			.willReturn(book);
		given(bookGroupRepository.save(any()))
			.willReturn(bookGroup);

		// When
		var bookGroupId = defaultBookGroupService.createBookGroup(user.getId(), request);

		// Then
		verify(bookService).createBook(bookCreateRequest);
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
			startDate, endDate, false, true, maxMemberCount, currentMemberCount, commentCount,
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
			6, "북그룹", "소개합니다", true, "월든 작가는?", "헨리데이빗소로우", true
		);
		given(bookGroupRepository.findById(1L))
			.willReturn(Optional.of(bookGroup));
		//when
		Optional<BookGroup> bookGroupOptional = defaultBookGroupService.findById(1L);
		//then
		assertEquals(bookGroupOptional.get(), bookGroup);
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
}