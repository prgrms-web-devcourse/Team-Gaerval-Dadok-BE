package com.dadok.gaerval.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.dto.response.SummaryBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@DisplayName("bookshelf service 테스트")
@ExtendWith(MockitoExtension.class)
class DefaultBookshelfServiceSliceTest {

	@InjectMocks
	private DefaultBookshelfService bookshelfService;

	@Mock
	private BookshelfRepository bookshelfRepository;

	@Mock
	private BookshelfItemRepository bookshelfItemRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserService userService;

	private final User user = UserObjectProvider.createKakaoUser();
	private final Bookshelf bookshelf = Bookshelf.create(user);
	private final Book book = BookObjectProvider.createRequiredFieldBook();

	@DisplayName("getById - id로 책장을 조회한다 - 성공")
	@Test
	void getById_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));

		// When
		var findBookshelf = bookshelfService.getById(1L);

		// Then
		assertThat(findBookshelf).isEqualTo(bookshelf);
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("getById - 존재하지않는 id로 책장을 조회한다- 실패")
	@Test
	void getById_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.getById(1L));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("findById - id로 책장을 조회한다 -  성공")
	@Test
	void findById_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));

		// When
		var findBookshelf = bookshelfService.findById(1L);

		// Then
		assertThat(findBookshelf).isNotEmpty();
		assertThat(findBookshelf.get()).isEqualTo(bookshelf);
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("createBookshelf - 책장을 생성한다. - 성공")
	@Test
	void createBookshelf_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		given(bookshelfRepository.save(any()))
			.willReturn(bookshelf);

		// When
		var id = bookshelfService.createBookshelf(user);

		// Then
		assertThat(id).isEqualTo(bookshelf.getId());
		verify(bookshelfRepository).save(any());
	}

	@DisplayName("insertBookSelfItem - book을 찾아 책장에 추가한다 -  성공")
	@Test
	void insertBookSelfItem_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findByIsbn(book.getIsbn()))
			.willReturn(Optional.of(book));

		// When
		var bookshelfId = bookshelfService.insertBookSelfItem(user, 1L, bookCreateRequest);

		// Then
		assertThat(bookshelfId).isEqualTo(1L);
		assertThat(bookshelf.getBookshelfItems()).hasSize(1);
		assertThat(bookshelf.getBookshelfItems().get(0).getBook()).isEqualTo(book);
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findByIsbn(book.getIsbn());
	}

	@DisplayName("insertBookSelfItem - book을 저장하고 책장에 추가한다 - 성공")
	@Test
	void insertBookSelfItem_bookSave_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findByIsbn(book.getIsbn()))
			.willReturn(Optional.empty());
		given(bookService.createBook(bookCreateRequest))
			.willReturn(book);

		// When
		var bookshelfId = bookshelfService.insertBookSelfItem(user, 1L, bookCreateRequest);

		// Then
		assertThat(bookshelfId).isEqualTo(1L);
		assertThat(bookshelf.getBookshelfItems()).hasSize(1);
		assertThat(bookshelf.getBookshelfItems().get(0).getBook()).isEqualTo(book);
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findByIsbn(book.getIsbn());
		verify(bookService).createBook(bookCreateRequest);
	}

	@DisplayName("insertBookSelfItem - 존재하지 않는 책장 id의 경우 - 실패")
	@Test
	void insertBookSelfItem_bookshelfNotExist_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.insertBookSelfItem(user, 1L, bookCreateRequest));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("insertBookSelfItem - 책장의 user가 현 사용자와 다를 경우 - 실패")
	@Test
	void insertBookSelfItem_bookshelfNotMatchedUser_fail() {
		// Given
		User otherUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(otherUser, "id", 645L);
		ReflectionTestUtils.setField(user, "id", 23L);
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));

		// When
		assertThrows(BookshelfUserNotMatchedException.class,
			() -> bookshelfService.insertBookSelfItem(otherUser, 1L, bookCreateRequest));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("removeBookSelfItem - 성공")
	@Test
	void removeBookSelfItem_success() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		BookshelfItem bookshelfItem = BookshelfItem.create(bookshelf, book, BookshelfItemType.READ);
		ReflectionTestUtils.setField(bookshelfItem, "id", 200L);

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.of(book));
		given(bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book))
			.willReturn(Optional.of(bookshelfItem));
		doNothing().when(bookshelfItemRepository).deleteById(200L);

		// When
		var bookshelfId = bookshelfService.removeBookSelfItem(user, 1L, book.getId());

		// Then
		assertThat(bookshelfId).isEqualTo(1L);
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
		verify(bookshelfItemRepository).findByBookshelfAndBook(bookshelf, book);
		verify(bookshelfItemRepository).deleteById(200L);
	}

	@DisplayName("removeBookSelfItem - 존재하지 않는 책 id - 실패")
	@Test
	void removeBookSelfItem_bookNotExist_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.removeBookSelfItem(user, 1L, book.getId()));

		// Then;
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
	}

	@DisplayName("removeBookSelfItem - 실패")
	@Test
	void removeBookSelfItem_bookshelfItemNotExist_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.of(book));
		given(bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.removeBookSelfItem(user, 1L, book.getId()));

		// Then
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
		verify(bookshelfItemRepository).findByBookshelfAndBook(bookshelf, book);
	}

	@DisplayName("findPopularBookshelvesByJob - 직업군에 맞는 책장 리스트 조회 - 성공")
	@Test
	void findPopularBookshelvesByJob_success() {
		// Given
		String jobGroup = JobGroup.HR.getDescription();
		Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("bookshelfItems.size")));

		var books = List.of(
			new SummaryBookshelfResponse.SummaryBookResponse(1L, "제목1", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(2L, "제목2", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(3L, "제목3", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(4L, "제목4", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(5L, "제목5", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(6L, "제목6", "url")
		);
		var Bookshelves = List.of(
			new SummaryBookshelfResponse(1L, "책장", books),
			new SummaryBookshelfResponse(2L, "책장", books),
			new SummaryBookshelfResponse(3L, "책장", books),
			new SummaryBookshelfResponse(4L, "책장", books),
			new SummaryBookshelfResponse(5L, "책장", books));

		given(bookshelfRepository.findAllByJob(JobGroup.HR, pageable, user.getId()))
			.willReturn(Bookshelves);

		// When
		var responses = bookshelfService.findPopularBookshelvesByJob(user, jobGroup);

		// Then
		verify(bookshelfRepository).findAllByJob(JobGroup.HR, pageable, user.getId());

		assertThat(responses.jobGroup()).isEqualTo(jobGroup);
		assertThat(responses.bookshelfResponses()).hasSize(5);
		assertThat(responses.bookshelfResponses().get(0).getBooks()).hasSize(5);

	}

	@DisplayName("findPopularBookshelvesByJob- 존재하지 않은 직업군의 책장 리스트 조회 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"개 발 ", "백수", "노 직군", "영지", "다독"})
	void findPopularBookshelvesByJob_JobGroupNotExist_fail(String jobGroup) {

		assertThrows(InvalidArgumentException.class,
			() -> bookshelfService.findPopularBookshelvesByJob(user, jobGroup));

	}

	@DisplayName("findSummaryBookshelf - user를 입력받야 책장 요약 데이터 조회 - 성공")
	@Test
	void findSummaryBookshelf_user_success() {
		// Given
		var books = List.of(
			new SummaryBookshelfResponse.SummaryBookResponse(1L, "제목1", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(2L, "제목2", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(3L, "제목3", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(4L, "제목4", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(5L, "제목5", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(6L, "제목6", "url")
		);

		var summaryBookshelf = new SummaryBookshelfResponse(2L, "찐 개발자 책장", books);

		given(bookshelfRepository.findByUser(user))
			.willReturn(summaryBookshelf);

		// When// Then
		var response = assertDoesNotThrow(() -> bookshelfService.findSummaryBookshelf(user));

		verify(bookshelfRepository).findByUser(user);
		assertThat(response.getBookshelfId()).isEqualTo(2L);
		assertThat(response.getBookshelfName()).isEqualTo("찐 개발자 책장");
		assertThat(response.getBooks()).hasSize(5);
		assertThat(response.getBooks().get(0).bookId()).isEqualTo(1L);
	}

	@DisplayName("findSummaryBookshelf - userId를 입력받야 책장 요약 데이터 조회 - 성공")
	@Test
	void findSummaryBookshelf_userId_success() {
		// Given
		var books = List.of(
			new SummaryBookshelfResponse.SummaryBookResponse(1L, "제목1", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(2L, "제목2", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(3L, "제목3", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(4L, "제목4", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(5L, "제목5", "url"),
			new SummaryBookshelfResponse.SummaryBookResponse(6L, "제목6", "url")
		);

		var summaryBookshelf = new SummaryBookshelfResponse(2L, "찐 개발자 책장", books);

		given(userService.getById(user.getId()))
			.willReturn(user);
		given(bookshelfRepository.findByUser(user))
			.willReturn(summaryBookshelf);

		// When// Then
		var response = assertDoesNotThrow(() -> bookshelfService.findSummaryBookshelf(user.getId()));

		verify(userService).getById(user.getId());
		verify(bookshelfRepository).findByUser(user);
		assertThat(response.getBookshelfId()).isEqualTo(2L);
		assertThat(response.getBookshelfName()).isEqualTo("찐 개발자 책장");
		assertThat(response.getBooks()).hasSize(5);
		assertThat(response.getBooks().get(0).bookId()).isEqualTo(1L);
	}

}