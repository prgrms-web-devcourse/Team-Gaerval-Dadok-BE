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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookInShelfResponses;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
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
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findByIsbn(book.getIsbn()))
			.willReturn(Optional.of(book));

		// When
		var bookshelfId = bookshelfService.insertBookSelfItem(user.getId(), 1L, bookCreateRequest);

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
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));
		given(bookService.findByIsbn(book.getIsbn()))
			.willReturn(Optional.empty());
		given(bookService.createBook(bookCreateRequest))
			.willReturn(book);

		// When
		var bookshelfId = bookshelfService.insertBookSelfItem(user.getId(), 1L, bookCreateRequest);

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
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.empty());

		// When
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.insertBookSelfItem(user.getId(), 1L, bookCreateRequest));

		// Then
		verify(bookshelfRepository).findById(1L);
	}

	@DisplayName("insertBookSelfItem - 책장의 user가 현 사용자와 다를 경우 - 실패")
	@Test
	void insertBookSelfItem_bookshelfNotMatchedUser_fail() {
		// Given
		ReflectionTestUtils.setField(user, "id", 23L);
		ReflectionTestUtils.setField(bookshelf, "id", 1L);
		var bookCreateRequest = new BookCreateRequest(book.getTitle(), book.getAuthor(), book.getIsbn(),
			book.getContents(), book.getUrl(), book.getImageUrl(), book.getPublisher(), book.getApiProvider());

		given(bookshelfRepository.findById(1L))
			.willReturn(Optional.of(bookshelf));

		// When
		assertThrows(BookshelfUserNotMatchedException.class,
			() -> bookshelfService.insertBookSelfItem(645L, 1L, bookCreateRequest));

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

	@DisplayName("findPopularBookshelvesByJob - 직업군에 맞는 책장 리스트 조회 - 성공")
	@Test
	void findPopularBookshelvesByJob_success() {
		// Given
		String jobGroup = JobGroup.HR.getDescription();
		Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("bookshelfItems.size")));

		var bookshelf = Bookshelf.create(user);
		ReflectionTestUtils.setField(bookshelf, "id", 2L);
		BookshelfItem.create(bookshelf, book);

		List<Bookshelf> bookshelves = List.of(bookshelf);

		given(bookshelfRepository.findAllByJob(JobGroup.HR, pageable, 5L))
			.willReturn(bookshelves);

		// When
		var responses = bookshelfService.findPopularBookshelvesByJob(5L, jobGroup);

		// Then
		verify(bookshelfRepository).findAllByJob(JobGroup.HR, pageable, 5L);

		assertThat(responses.jobGroup()).isEqualTo(jobGroup);
		assertThat(responses.bookshelfResponses()).hasSize(1);
		assertThat(responses.bookshelfResponses().get(0).books()).hasSize(1);

	}

	@DisplayName("findPopularBookshelvesByJob- 존재하지 않은 직업군의 책장 리스트 조회 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"개 발 ", "백수", "노 직군", "영지", "다독"})
	void findPopularBookshelvesByJob_JobGroupNotExist_fail(String jobGroup) {

		assertThrows(InvalidArgumentException.class,
			() -> bookshelfService.findPopularBookshelvesByJob(user.getId(), jobGroup));

	}

	@DisplayName("findSummaryBookshelf - userId를 입력받야 책장 요약 데이터 조회 - 성공")
	@Test
	void findSummaryBookshelf_userId_success() {
		// Given
		var summaryResponse = new SummaryBookshelfResponse(23L, "영지님의 책장",
			List.of(new SummaryBookshelfResponse.SummaryBookResponse(1L, "해리포터",
				"https://www.producttalk.org/wp-content/uploads/2018/06/www.maxpixel.net-Ears-Zoo-Hippopotamus-Eye-Animal-World-Hippo-2878867.jpg"))
		);

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
		Long userId = 1L;

		BookShelfDetailResponse bookShelfDetailResponse = new BookShelfDetailResponse(1L, "책장이름", true, userId,
			"username", "userNickname",
			"http://dadok.com/images", JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER, 5);

		given(bookshelfRepository.findByIdWithUserAndJob(userId))
			.willReturn(Optional.of(bookShelfDetailResponse));
		//when

		BookShelfDetailResponse bookShelfWithJob = bookshelfService.findBookShelfWithJob(userId);

		//then
		assertEquals(bookShelfWithJob, bookShelfDetailResponse);
	}

	@DisplayName("findBookShelfWithJob - userId로 책장과 유저와 직업을 조회했을 때 없다면 예외를 던진다.")
	@Test
	void findBookShelfWithJob_throw() {
		//given
		Long userId = 1L;

		given(bookshelfRepository.findByIdWithUserAndJob(userId))
			.willReturn(Optional.empty());
		//when
		assertThrows(ResourceNotfoundException.class,
			() -> bookshelfService.findBookShelfWithJob(userId));
	}

}