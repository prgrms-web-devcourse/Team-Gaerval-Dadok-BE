package com.dadok.gaerval.domain.book_group.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.book_group.repository.BookGroupCommentRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
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
}