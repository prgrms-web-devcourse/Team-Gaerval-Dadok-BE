package com.dadok.gaerval.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.request.BookshelfItemCreateRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.JobObjectProvider;
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

	private final User user = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());
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
		var request = new BookshelfItemCreateRequest(book.getId());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.of(book));
		given(bookshelfItemRepository.existsByBookshelfIdAndBookId(1L, book.getId()))
			.willReturn(false);

		// When
		var bookshelfId = bookshelfService.insertBookSelfItem(user.getId(), 1L, request);

		// Then
		assertThat(bookshelfId).isEqualTo(1L);
		assertThat(bookshelf.getBookshelfItems()).hasSize(1);
		assertThat(bookshelf.getBookshelfItems().get(0).getBook()).isEqualTo(book);
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
	}

	@DisplayName("insertBookSelfItem - book이 존재하지 않는 경우 - 실패")
	@Test
	void insertBookSelfItem_bookNotExist_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var request = new BookshelfItemCreateRequest(book.getId());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.insertBookSelfItem(user.getId(), 1L, request));

		// Then
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
	}

	@DisplayName("insertBookSelfItem - 존재하지 않는 책장 id의 경우 - 실패")
	@Test
	void insertBookSelfItem_bookshelfNotExist_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var request = new BookshelfItemCreateRequest(book.getId());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.insertBookSelfItem(user.getId(), 1L, request));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("insertBookSelfItem - 책장의 user가 현 사용자와 다를 경우 - 실패")
	@Test
	void insertBookSelfItem_bookshelfNotMatchedUser_fail() {
		// Given
		ReflectionTestUtils.setField(user, "id", 23L);
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var request = new BookshelfItemCreateRequest(book.getId());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));

		// When
		assertThrows(BookshelfUserNotMatchedException.class,
			() -> bookshelfService.insertBookSelfItem(645L, 1L, request));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("insertBookSelfItem - 이미 포함된 책 일 경우 - 실패")
	@Test
	void insertBookSelfItem_alreadyContain_fail() {
		// Given
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var request = new BookshelfItemCreateRequest(book.getId());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findById(book.getId()))
			.willReturn(Optional.of(book));
		given(bookshelfItemRepository.existsByBookshelfIdAndBookId(1L, book.getId()))
			.willReturn(true);

		// When
		assertThrows(AlreadyContainBookshelfItemException.class, () ->
			bookshelfService.insertBookSelfItem(user.getId(), 1L, request)
		);

		// Then
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
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
		var bookshelfId = bookshelfService.removeBookSelfItem(user.getId(), 1L, book.getId());

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
			() -> bookshelfService.removeBookSelfItem(user.getId(), 1L, book.getId()));

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
			() -> bookshelfService.removeBookSelfItem(user.getId(), 1L, book.getId()));

		// Then
		verify(bookshelfRepository).findById(1L);
		verify(bookService).findById(book.getId());
		verify(bookshelfItemRepository).findByBookshelfAndBook(bookshelf, book);
	}

	@DisplayName("findSuggestionBookshelvesByJobGroup - 직업군에 맞는 책장 리스트 조회 - 성공")
	@Test
	void findSuggestionBookshelvesByJobGroup_success() {
		// Given
		var jobGroup = JobGroup.HR;

		var expectResponses = List.of(new BookShelfSummaryResponse(23L, "영지님의 책장",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
			, 5));

		given(bookshelfRepository.findSuggestionsByJobGroup(JobGroup.HR, 5L, 5))
			.willReturn(expectResponses);

		// When
		var responses = bookshelfService.findSuggestionBookshelvesByJobGroup(5L, jobGroup);

		// Then
		verify(bookshelfRepository).findSuggestionsByJobGroup(JobGroup.HR, 5L, 5);

		assertThat(responses.jobGroup()).isEqualTo(jobGroup);
		assertThat(responses.bookshelfResponses()).hasSize(1);
		assertThat(responses.bookshelfResponses().get(0).books()).hasSize(1);

	}

	@DisplayName("findSuggestionBookshelves - 인기 책장 리스트 조회 - 성공")
	@Test
	void findSuggestionBookshelves_success() {
		// Given
		String jobGroup = JobGroup.HR.getDescription();

		var expectResponses = List.of(new BookShelfSummaryResponse(23L, "영지님의 책장",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
			, 5));

		given(bookshelfRepository.findAllSuggestions(5))
			.willReturn(expectResponses);

		// When
		var responses = bookshelfService.findSuggestionBookshelves();

		// Then
		verify(bookshelfRepository).findAllSuggestions(5);

		assertThat(responses.bookshelfResponses()).hasSize(1);
		assertThat(responses.bookshelfResponses().get(0).books()).hasSize(1);
	}

	@DisplayName("findSummaryBookshelf - userId를 입력받야 책장 요약 데이터 조회 - 성공")
	@Test
	void findSummaryBookshelf_userId_success() {
		// Given
		var summaryResponse = new BookShelfSummaryResponse(23L, "영지님의 책장",
			List.of(new BookShelfSummaryResponse.BookSummaryResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg")),
			5);

		given(bookshelfRepository.findSummaryById(user.getId()))
			.willReturn(Optional.of(summaryResponse));

		// When// Then
		var response = assertDoesNotThrow(() -> bookshelfService.findSummaryBookshelf(user.getId()));

		verify(bookshelfRepository).findSummaryById(user.getId());
		assertThat(response.bookshelfId()).isEqualTo(23L);
		assertThat(response.books()).hasSize(1);
	}

	@DisplayName("findSummaryBookshelf - userId를 입력받야 책장 요약 데이터 조회 - 실패")
	@Test
	void findSummaryBookshelf_userId_fail() {
		// Given
		given(bookshelfRepository.findSummaryById(user.getId()))
			.willReturn(Optional.empty());

		// When// Then
		assertThrows(ResourceNotfoundException.class, () -> bookshelfService.findSummaryBookshelf(user.getId()));

		verify(bookshelfRepository).findSummaryById(user.getId());
	}

	@DisplayName("findAllBooksInShelf - 요청에 맞는 결과가 없으면 빈 응답을 반환한다.")
	@Test
	void findAllBooksInShelf_empty() {
		//given
		Long bookShelfId = 1L;
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);

		given(bookshelfItemRepository.findAllInBookShelf(bookShelfId, booksInBookShelfFindRequest))
			.willReturn(new SliceImpl<>(new ArrayList<>()));

		//when
		BookInShelfResponses bookInShelfResponses = bookshelfService.findAllBooksInShelf(bookShelfId,
			booksInBookShelfFindRequest);

		//then
		assertThat(bookInShelfResponses)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("count", 0)
			.hasFieldOrPropertyWithValue("isEmpty", true)
			.hasFieldOrPropertyWithValue("books", Collections.emptyList());
	}

	@DisplayName("findAllBooksInShelf - 책장안에 있는 책들을 요청만큼 가져온다.")
	@Test
	void findAllBooksInShelf() {
		//given
		Long bookShelfId = 1L;
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);

		Book book1 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book1, "id", 1L);
		Book book2 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book2, "id", 2L);
		Book book3 = BookObjectProvider.createAllFieldBook();
		ReflectionTestUtils.setField(book3, "id", 3L);

		User kakaoUser = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());
		ReflectionTestUtils.setField(kakaoUser, "id", 1L);

		Bookshelf createBookShelf = Bookshelf.create(kakaoUser);
		ReflectionTestUtils.setField(createBookShelf, "id", bookShelfId);
		List<BookshelfItem> bookshelfItems = List.of(
			BookshelfItem.create(createBookShelf, book1, BookshelfItemType.WISH),
			BookshelfItem.create(createBookShelf, book2, BookshelfItemType.WISH),
			BookshelfItem.create(createBookShelf, book3, BookshelfItemType.READ));

		given(bookshelfItemRepository.findAllInBookShelf(bookShelfId, booksInBookShelfFindRequest))
			.willReturn(new SliceImpl<>(bookshelfItems, PageRequest.of(0, 50,
				Sort.by(Sort.Direction.DESC, "id")), false));

		//when
		BookInShelfResponses bookInShelfResponses = bookshelfService.findAllBooksInShelf(bookShelfId,
			booksInBookShelfFindRequest);

		//then
		assertThat(bookInShelfResponses)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("count", 3)
			.hasFieldOrPropertyWithValue("isEmpty", false);
	}

	@DisplayName("findBookShelfWithJob - userId로 책장과 유저와 직업을 같이 조회해온다.")
	@Test
	void findBookShelfWithJob() {
		//given
		Long ownerId = 1L;
		Long userId = 2L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "책장이름", true, 3L, false,
			userId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfRepository.findByIdWithUserAndJob(ownerId, userId))
			.willReturn(Optional.of(bookShelfDetailResponse));
		//when

		BookShelfDetailResponse bookShelfWithJob = bookshelfService.findBookShelfWithJob(ownerId, userId);

		//then
		assertEquals(bookShelfWithJob, bookShelfDetailResponse);
	}

	@DisplayName("findBookShelfWithJob - ownerId로 책장과 유저와 직업을 조회했을 때 없다면 예외를 던진다.")
	@Test
	void findBookShelfWithJob_throw() {
		//given
		Long ownerId = 1L;
		Long userId = 1L;

		given(bookshelfRepository.findByIdWithUserAndJob(ownerId, userId))
			.willReturn(Optional.empty());
		//when
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.findBookShelfWithJob(ownerId, userId));
	}

	@DisplayName("updateJobIdByUserId - 책장을 찾을 수 없으므로 책장을 생성하고 변경에 성공한다. ")
	@Test
	void updateJobIdByUserId_createBook() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Job job = JobObjectProvider.backendJob();
		long userId = 1L;
		ReflectionTestUtils.setField(kakaoUser, "id", userId);
		Bookshelf willBookshelf = Bookshelf.create(kakaoUser);

		given(bookshelfRepository.findByUserId(userId))
			.willReturn(Optional.empty());

		given(bookshelfRepository.save(willBookshelf))
			.willReturn(willBookshelf);

		//when
		bookshelfService.updateJobIdByUserId(kakaoUser, job.getId());

		//then
		verify(bookshelfRepository).findByUserId(userId);
		assertEquals(willBookshelf.getJobId(), job.getId());
	}

	@DisplayName("updateJobIdByUserId - 책장의 jobId 변경에 성공한다.")
	@Test
	void updateJobIdByUserId_success() {
		//given
		User kakaoUser = UserObjectProvider.createKakaoUser();
		Job job = JobObjectProvider.backendJob();
		ReflectionTestUtils.setField(kakaoUser, "job", job);
		Long idToUpdate = 10L;
		long userId = 1L;
		ReflectionTestUtils.setField(kakaoUser, "id", userId);

		Bookshelf bookShelf = Bookshelf.create(kakaoUser);

		given(bookshelfRepository.findByUserId(userId))
			.willReturn(Optional.of(bookShelf));

		//when
		bookshelfService.updateJobIdByUserId(kakaoUser, idToUpdate);
		//then
		assertThat(bookShelf).hasFieldOrPropertyWithValue("jobId", idToUpdate);
		verify(bookshelfRepository).findByUserId(userId);
	}

	@DisplayName("findBookShelfById - bookshelfId로 책장과 유저와 직업을 같이 조회해온다.")
	@Test
	void findBookShelfById() {
		//given
		Long userId = 1L;
		Long bookshelfId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "책장이름", true, 3L, false,
			userId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfRepository.findBookShelfById(bookshelfId, userId))
			.willReturn(Optional.of(bookShelfDetailResponse));
		//when

		BookShelfDetailResponse bookShelfWithJob = bookshelfService.findBookShelfById(bookshelfId, userId);

		//then
		assertEquals(bookShelfWithJob, bookShelfDetailResponse);
	}

	@DisplayName("findBookShelfById - bookshelfId로 책장과 유저와 직업을 조회했을 때 없다면 예외를 던진다.")
	@Test
	void findBookShelfById_throw() {
		//given
		Long userId = 1L;
		Long bookshelfId = 1L;

		given(bookshelfRepository.findBookShelfById(bookshelfId, userId))
			.willReturn(Optional.empty());
		//when
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.findBookShelfById(bookshelfId, userId));
	}

	@DisplayName("existsByUserIdAndBookId - 유저 ID와 책 ID로 존재 여부 확인에 성공한다. ")
	@Test
	void existsByUserIdAndBookId() {
		//given
		Long userId = 1L;
		Long bookId = 2L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "id", userId);
		Bookshelf bookShelf = Bookshelf.create(kakaoUser);
		ReflectionTestUtils.setField(bookShelf, "id", 10L);

		given(bookshelfRepository.findByUserId(userId))
			.willReturn(Optional.of(bookshelf));
		given(bookshelfItemRepository.existsByBookshelfIdAndBookId(bookshelf.getId(), bookId))
			.willReturn(true);

		//when
		boolean result = bookshelfService.existsByUserIdAndBookId(userId, bookId);

		//then
		assertThat(result).isTrue();
	}

}