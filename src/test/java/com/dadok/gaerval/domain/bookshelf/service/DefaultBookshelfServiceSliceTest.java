package com.dadok.gaerval.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfRepository;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.testutil.BookObjectProvider;

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

	private final User user = User.builder().userAuthority(UserAuthority.of(Role.USER)).build();
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
		User otherUser = User.builder().name("티나").userAuthority(UserAuthority.of(Role.USER)).build();
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
}