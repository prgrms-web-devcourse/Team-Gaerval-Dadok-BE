package com.dadok.gaerval.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.book.exception.AlreadyContainBookCommentException;
import com.dadok.gaerval.domain.book.exception.NotMarkedBookException;
import com.dadok.gaerval.domain.book.repository.BookCommentRepository;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.testutil.BookCommentObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class BookCommentServiceSliceTest {

	@Mock
	private BookCommentRepository bookCommentRepository;

	@Mock
	private UserService userService;

	@Mock
	private BookService bookService;

	@InjectMocks
	private DefaultBookCommentService bookCommentService;

	@Mock
	private BookshelfService bookshelfService;

	@DisplayName("createBookComment - 도서 리뷰를 저장하는데 성공한다.")
	@Test
	void createBookComment() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);
		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();
		BookComment comment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		ReflectionTestUtils.setField(comment, "id", 1L);

		given(userService.getById(1L)).willReturn(user);
		given(bookService.getById(1L)).willReturn(book);
		given(bookshelfService.existsByUserIdAndBookId(1L, 1L)).willReturn(true);
		given(bookCommentRepository.save(any())).willReturn(
			comment);

		// when
		Long savedCommentId = bookCommentService.createBookComment(book.getId(), user.getId(),
			bookCommentCreateRequest);

		// then
		verify(userService).getById(1L);
		verify(bookService).getById(1L);
		verify(bookCommentRepository).existsByBookIdAndUserId(1L, 1L);
		verify(bookCommentRepository).save(any());
		assertEquals(comment.getId(), savedCommentId);
	}

	@DisplayName("createBookComment - 북마크가 없는 경우 도서 리뷰를 생성하는데 실패한다.")
	@Test
	void create_NotBookMarked_BookComment_Failure() {
		// given
		Long userId = 1L;
		Long bookId = 1L;
		User user = UserObjectProvider.createKakaoUser();
		Book book = BookObjectProvider.createRequiredFieldBook();
		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();

		given(userService.getById(userId)).willReturn(user);
		given(bookService.getById(bookId)).willReturn(book);
		given(bookshelfService.existsByUserIdAndBookId(userId, bookId)).willReturn(false);

		// when
		Executable executable = () -> bookCommentService.createBookComment(bookId, userId, bookCommentCreateRequest);

		// then
		assertThrows(NotMarkedBookException.class, executable);
	}

	@DisplayName("createBookComment - 이미 도서 리뷰가 있는 경우 도서 리뷰를 생성하는데 실패한다.")
	@Test
	void create_AlreadyExistBookComment_Failure() {
		// given
		Long userId = 1L;
		Long bookId = 1L;
		User user = UserObjectProvider.createKakaoUser();
		Book book = BookObjectProvider.createRequiredFieldBook();
		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();

		given(userService.getById(userId)).willReturn(user);
		given(bookService.getById(bookId)).willReturn(book);
		given(bookshelfService.existsByUserIdAndBookId(userId, bookId)).willReturn(true);
		given(bookCommentRepository.existsByBookIdAndUserId(bookId, userId)).willReturn(true);

		// when
		Executable executable = () -> bookCommentService.createBookComment(bookId, userId, bookCommentCreateRequest);

		// then
		assertThrows(AlreadyContainBookCommentException.class, executable);
	}


	@DisplayName("updateBookComment - 도서 리뷰를 수정하는데 성공한다.")
	@Test
	void updateBookComment() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();
		BookCommentUpdateRequest bookCommentUpdateRequest = BookCommentObjectProvider.createCommentUpdateRequest();

		BookComment comment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		comment.changeComment(bookCommentCreateRequest.comment());
		ReflectionTestUtils.setField(comment, "id", 1L);

		given(bookshelfService.existsByUserIdAndBookId(1L, 1L)).willReturn(true);
		given(bookCommentService.updateBookComment(1L, 1L, bookCommentUpdateRequest)).willReturn(
			BookCommentObjectProvider.createMockResponses().get(0)
		);


		// when
		BookCommentResponse updatedComment = bookCommentService.updateBookComment(book.getId(), user.getId(),
			bookCommentUpdateRequest);

		// then
		assertEquals(bookCommentUpdateRequest.comment(), updatedComment.getContents());
	}


	@DisplayName("updateBookComment - 북마크에서 해제 되었지만 존재하는 도서 리뷰를 수정하는데 성공한다.")
	@Test
	void update_NotBookMarked_BookComment_Success() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookCommentCreateRequest bookCommentCreateRequest = BookCommentObjectProvider.createBookCommentCreateRequest();
		BookCommentUpdateRequest bookCommentUpdateRequest = BookCommentObjectProvider.createCommentUpdateRequest();

		BookComment comment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		comment.changeComment(bookCommentCreateRequest.comment());
		ReflectionTestUtils.setField(comment, "id", 1L);

		given(bookCommentRepository.existsByBookIdAndUserId(1L, 1L)).willReturn(true);
		given(bookshelfService.existsByUserIdAndBookId(1L, 1L)).willReturn(false);
		given(bookCommentService.updateBookComment(1L, 1L, bookCommentUpdateRequest)).willReturn(
			BookCommentObjectProvider.createMockResponses().get(0)
		);


		// when
		BookCommentResponse updatedComment = bookCommentService.updateBookComment(book.getId(), user.getId(),
			bookCommentUpdateRequest);

		// then
		assertEquals(bookCommentUpdateRequest.comment(), updatedComment.getContents());
	}


	@DisplayName("updateBookComment - 북마크도 없고 리뷰도 존재하지 않으면 수정하는데 실패한다.")
	@Test
	void update_NotBookMarked_BookComment_Failure() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookCommentUpdateRequest bookCommentUpdateRequest = BookCommentObjectProvider.createCommentUpdateRequest();

		given(bookCommentRepository.existsByBookIdAndUserId(1L, 1L)).willReturn(false);
		given(bookshelfService.existsByUserIdAndBookId(1L, 1L)).willReturn(false);

		// when
		Executable executable = () -> {
			bookCommentService.updateBookComment(1L, 1L, bookCommentUpdateRequest);
			verify(bookCommentRepository).existsByBookIdAndUserId(1L, 1L);
			verify(bookshelfService).existsByUserIdAndBookId(1L, 1L);
		};

		// then
		assertThrows(NotMarkedBookException.class, executable);
	}

	@DisplayName("deleteBookComment - 도서 리뷰를 삭제하는데 성공한다.")
	@Test
	void deleteBookComment() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookComment comment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		ReflectionTestUtils.setField(comment, "id", 1L);

		given(bookCommentRepository.findByBookId(1L, 1L)).willReturn(Optional.of(comment));

		// when
		bookCommentService.deleteBookComment(book.getId(), user.getId(),
			1L);

		// then
		verify(bookCommentRepository).findByBookId(1L, 1L);
		verify(bookCommentRepository).delete(comment);
	}

	@DisplayName("getById - 존재하는 도서 리뷰를 조회하는데 성공한다.")
	@Test
	void getById() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);
		BookComment existingComment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		ReflectionTestUtils.setField(existingComment, "id", 1L);
		given(bookCommentRepository.findById(1L)).willReturn(Optional.of(existingComment));

		// when
		BookComment comment = bookCommentService.getById(existingComment.getId());

		// then
		verify(bookCommentRepository).findById(1L);
		assertEquals(existingComment.getId(), comment.getId());
		assertEquals(existingComment.getUser(), comment.getUser());
		assertEquals(existingComment.getBook(), comment.getBook());
	}

	@DisplayName("findById - 존재하는 도서 리뷰 ID로 도서 리뷰를 찾는데 성공한다.")
	@Test
	void findById_WithExistingId_ShouldReturnBookComment() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);
		BookComment existingComment = BookCommentObjectProvider.create(user, book, BookCommentObjectProvider.comment1);
		ReflectionTestUtils.setField(existingComment, "id", 1L);
		given(bookCommentRepository.findById(1L)).willReturn(Optional.of(existingComment));

		given(bookCommentRepository.findById(1L)).willReturn(Optional.of(existingComment));

		// when
		BookComment result = bookCommentService.findById(1L).orElseThrow();

		// then
		verify(bookCommentRepository).findById(1L);
		assertEquals(existingComment, result);
	}

	@DisplayName("findById - 존재하지 않는 도서 리뷰 ID로 도서 리뷰를 찾는데 실패한다.")
	@Test
	void findById_WithNonExistingId_ShouldReturnEmpty() {
		// given
		Long bookCommentId = 1L;

		given(bookCommentRepository.findById(bookCommentId)).willReturn(Optional.empty());

		// when
		Optional<BookComment> result = bookCommentService.findById(bookCommentId);

		// then
		verify(bookCommentRepository).findById(bookCommentId);
		assertTrue(result.isEmpty());
	}

	@DisplayName("findBookComments - 도서 리뷰 목록을 조회하는데 성공한다.")
	@Test
	void findBookComments() {
		// given
		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", 1L);
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1L);

		BookCommentSearchRequest bookCommentSearchRequest = new BookCommentSearchRequest(10, null, null);

		List<BookCommentResponse> bookComments = BookCommentObjectProvider.createMockResponses();

		Slice<BookCommentResponse> bookFindResponses = QueryDslUtil.toSlice(bookComments,
			PageRequest.of(0, 50, Sort.by(
				Sort.Direction.DESC, "comment_id"
			)));

		given(bookCommentRepository.findAllComments(1L, 1L, bookCommentSearchRequest)).willReturn(
			new BookCommentResponses(bookFindResponses));

		// when
		BookCommentResponses result = bookCommentService.findBookCommentsBy(book.getId(), user.getId(),
			bookCommentSearchRequest);

		// then
		verify(bookCommentRepository).findAllComments(1L, 1L, bookCommentSearchRequest);
		assertEquals(3, result.count());
	}

}