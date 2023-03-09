package com.dadok.gaerval.domain.book_group.service;

import static com.dadok.gaerval.testutil.BookGroupCommentObjectProvider.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.book_group.repository.BookGroupCommentRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.testutil.BookGroupCommentObjectProvider;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookGroupCommentServiceTest {

	@InjectMocks
	private DefaultBookGroupCommentService defaultBookGroupCommentService;

	@Mock
	private BookGroupCommentRepository bookGroupCommentRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserService userService;

	@Mock
	private BookGroupService bookGroupService;

	@DisplayName("findAllBookGroupCommentsByGroup - groupId로 groupComment 목록을 조회하는데 성공한다.")
	@Test
	void findAllBookGroupCommentsByGroup() {
		BookGroupCommentSearchRequest request = new BookGroupCommentSearchRequest(10, null, null);
		List<BookGroupCommentResponse> bookGroupCommentResponseList = BookGroupCommentObjectProvider.mockCommentResponses;

		List<BookGroupCommentResponse> sortedComments = bookGroupCommentResponseList.stream()
			.sorted(Comparator.comparing(BookGroupCommentResponse::getCommentId).reversed())
			.collect(Collectors.toList());

		Slice<BookGroupCommentResponse> commentResponses = QueryDslUtil.toSlice(sortedComments,
			PageRequest.of(0, request.pageSize(), Sort.by(
				Sort.Direction.DESC, "book_id"
			)));

		BookGroupCommentResponses bookGroupCommentResponses = new BookGroupCommentResponses(
			commentResponses);

		given(bookGroupCommentRepository.findAllBy(request, 234L, 234L))
			.willReturn(bookGroupCommentResponses);

		//when
		BookGroupCommentResponses findBookGroupCommentResponses = defaultBookGroupCommentService.findAllBookGroupCommentsByGroup(
			request, 234L, 234L);

		//then
		assertThat(findBookGroupCommentResponses)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", BookGroupCommentObjectProvider.mockCommentResponses.size())
			.hasFieldOrPropertyWithValue("isEmpty", false);

		assertThat(findBookGroupCommentResponses.bookGroupComments()).isSortedAccordingTo(
			(o1, o2) -> o2.getCommentId().compareTo(o1.getCommentId()));

	}

	@DisplayName("createBookGroupComment - 부모 없이 groupComment를 생성하는데 성공한다.")
	@Test
	void createBookGroupComment() {

		// given
		Long bookCommentId = BookGroupCommentObjectProvider.bookCommentId;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			234L);

		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());

		User user = UserObjectProvider.createKakaoUser();

		given(bookGroupCommentRepository.save(any()))
			.willReturn(groupComment);
		given(bookGroupService.getById(234L)).willReturn(bookGroup);
		given(userService.getById(234L)).willReturn(user);
		given(bookGroupService.checkGroupMember(234L, 234L)).willReturn(true);

		// when
		Long savedId = defaultBookGroupCommentService.createBookGroupComment(234L, 234L,
			new BookGroupCommentCreateRequest(null, BookGroupCommentObjectProvider.comment1));

		// then
		verify(bookGroupCommentRepository).save(any());
		assertEquals(bookCommentId, savedId);
	}


	@DisplayName("createBookGroupComment - 자식 groupComment를 생성하는데 성공한다.")
	@Test
	void createBookGroupChildComment() {

		// given
		Long bookCommentId = BookGroupCommentObjectProvider.bookCommentId;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			234L);

		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());

		GroupComment parentGroupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createNaverUser());

		User user = UserObjectProvider.createKakaoUser();

		given(bookGroupCommentRepository.save(any()))
			.willReturn(groupComment);
		given(bookGroupService.getById(234L)).willReturn(bookGroup);
		given(userService.getById(234L)).willReturn(user);
		given(bookGroupService.checkGroupMember(234L, 234L)).willReturn(true);
		given(bookGroupCommentRepository.existsBy(123L)).willReturn(true);
		given(defaultBookGroupCommentService.findById(123L)).willReturn(Optional.of(parentGroupComment));

		// when
		Long savedId = defaultBookGroupCommentService.createBookGroupComment(234L, 234L,
			new BookGroupCommentCreateRequest(123L, BookGroupCommentObjectProvider.comment1));

		// then
		verify(bookGroupCommentRepository).save(any());
		assertEquals(bookCommentId, savedId);
	}

	@DisplayName("createBookGroupComment - parentComment가 없는 1단계 댓글을 생성하는데 실패한다.")
	@Test
	void createBookGroupCommentFailureWithoutParentComment() {

		// given
		Long bookCommentId = BookGroupCommentObjectProvider.bookCommentId;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			234L);
		User user = UserObjectProvider.createKakaoUser();
		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup, user);

		given(bookGroupService.getById(234L)).willReturn(bookGroup);
		given(userService.getById(234L)).willReturn(user);
		given(bookGroupService.checkGroupMember(234L, 234L)).willReturn(true);
		given(bookGroupCommentRepository.existsBy(any())).willReturn(false);

		// when
		try {
			defaultBookGroupCommentService.createBookGroupComment(234L, 234L,
				new BookGroupCommentCreateRequest(123L, BookGroupCommentObjectProvider.comment1));
			fail(new ResourceNotfoundException(GroupComment.class));
		} catch (ResourceNotfoundException ex) {
			// then
			verify(bookGroupCommentRepository, never()).save(any());
		}
	}
	@DisplayName("getById - bookGroupCommentId로 조회에 성공한다.")
	@Test
	void getById() {
		// given
		Long bookCommentId = BookGroupCommentObjectProvider.bookCommentId;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			234L);
		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());
		given(bookGroupCommentRepository.findById(bookCommentId))
			.willReturn(Optional.of(groupComment));

		// when
		GroupComment findBookGroupComment = defaultBookGroupCommentService.getById(bookCommentId);

		// then
		verify(bookGroupCommentRepository).findById(bookCommentId);
		assertEquals(groupComment, findBookGroupComment);
	}

	@DisplayName("findById - bookGroupCommentId로 조회에 성공한다.")
	@Test
	void findById() {
		// given
		Long bookCommentId = BookGroupCommentObjectProvider.bookCommentId;
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			234L);
		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());
		given(bookGroupCommentRepository.findById(bookCommentId))
			.willReturn(Optional.of(groupComment));

		// when
		Optional<GroupComment> findBookGroupComment = defaultBookGroupCommentService.findById(bookCommentId);

		// then
		verify(bookGroupCommentRepository).findById(bookCommentId);
		assertTrue(findBookGroupComment.isPresent());
		assertEquals(groupComment, findBookGroupComment.get());
	}

	@DisplayName("updateBookGroupComment - 모임 댓글을 수정하는데 성공한다.")
	@Test
	void updateBookGroupComment() {
		String modifiedComment = "바꾼 댓글이에요";
		BookGroupCommentUpdateRequest groupCommentUpdateRequest = new BookGroupCommentUpdateRequest(bookCommentId, modifiedComment);
		BookGroupCommentResponse bookGroupCommentResponse = new BookGroupCommentResponse(bookCommentId, modifiedComment, 1L, null, 1L, UserObjectProvider.PICTURE_URL, LocalDateTime.of(2023, 3, 7, 12,11)
			, LocalDateTime.now(), "티나", true);

		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			1L);
		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());
		given(bookGroupCommentRepository.findById(bookCommentId))
			.willReturn(Optional.of(groupComment));
		given(bookGroupService.checkGroupMember(1L, 1L)).willReturn(true);
		given(bookGroupCommentRepository.findGroupComment(bookCommentId, 1L, 1L))
			.willReturn(bookGroupCommentResponse);

		//when
		BookGroupCommentResponse groupCommentUpdateResponse = defaultBookGroupCommentService.updateBookGroupComment(
			1L, 1L, groupCommentUpdateRequest);

		assertEquals(modifiedComment, groupCommentUpdateResponse.getContents());

	}


	@DisplayName("deleteBookGroupComment - 모임 댓글을 삭제하는데 성공한다.")
	@Test
	void deleteBookGroupComment() {
		BookGroupCommentDeleteRequest deleteRequest = new BookGroupCommentDeleteRequest(bookCommentId);

		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createRequiredFieldBook(),
			1L);
		GroupComment groupComment = BookGroupCommentObjectProvider.createSampleGroupComment(bookGroup,
			UserObjectProvider.createKakaoUser());

		given(bookGroupCommentRepository.findById(bookCommentId))
			.willReturn(Optional.of(groupComment));
		given(bookGroupService.checkGroupMember(1L, 1L)).willReturn(true);

		// when
		defaultBookGroupCommentService.deleteBookGroupComment(1L, 1L, deleteRequest);

		// then
		verify(bookGroupCommentRepository).findById(bookCommentId);
		verify(bookGroupCommentRepository).delete(groupComment);
	}
}