package com.dadok.gaerval.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.exception.BookApiNotAvailableException;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.global.config.externalapi.ExternalApiError;
import com.dadok.gaerval.global.config.externalapi.ExternalBookApiOperations;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private ExternalBookApiOperations externalBookApiOperations;

	@InjectMocks
	private DefaultBookService defaultBookService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookMapper bookMapper;

	@Mock
	private ObjectMapper objectMapper;

	@DisplayName("createBook - 도서를 저장하는데 성공한다.")
	@Test
	void createBook() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1234L);

		given(bookRepository.save(any()))
			.willReturn(book);

		// when
		BookCreateRequest bookCreateRequest = BookObjectProvider.createBookCreateRequest();
		Long savedBookId = defaultBookService.createBookAndReturnId(bookCreateRequest);

		// then
		verify(bookRepository).save(any());
		assertEquals(book.getId(), savedBookId);
	}

	@DisplayName("createBook - 이미 저장된 도서를 수정하는데 성공한다.")
	@Test
	void createBook_UpdateExistingBook() {
		// given
		Book existingBook = BookObjectProvider.createRequiredFieldBook();

		given(bookRepository.findBookByIsbn(anyString()))
			.willReturn(Optional.of(existingBook));

		Book updatedBook = BookObjectProvider.createRequiredFieldBook();
		updatedBook.changeUrl("https://newurl.com");
		updatedBook.changeContents("새로운 소개글");
		updatedBook.changeAuthor("새로운 작가");
		updatedBook.changeTitle("새로운 타이틀");
		updatedBook.changeImageUrl("https://newimageurl.com");
		updatedBook.changeApiProvider("ALADIN");

		given(bookRepository.save(any()))
			.willReturn(updatedBook);

		// when
		BookCreateRequest bookCreateRequest = BookObjectProvider.createBookCreateRequest();
		Long savedBookId = defaultBookService.createBookAndReturnId(bookCreateRequest);

		// then
		verify(bookRepository).findBookByIsbn(bookCreateRequest.isbn());
		verify(bookRepository).save(existingBook);
		assertEquals(existingBook.getId(), savedBookId);
	}

	@DisplayName("createBookAndReturnId - 도서를 저장하고 도서ID를 얻는데 성공한다.")
	@Test
	void createBookAndReturnId() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		ReflectionTestUtils.setField(book, "id", 1234L);

		given(bookRepository.save(any()))
			.willReturn(book);

		// when
		BookCreateRequest bookCreateRequest = BookObjectProvider.createBookCreateRequest();
		Long savedBookId = defaultBookService.createBookAndReturnId(bookCreateRequest);

		// then
		verify(bookRepository).save(any());
		assertEquals(book.getId(), savedBookId);
	}

	@DisplayName("getById - bookId로 조회에 성공한다.")
	@Test
	void getById() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		given(bookRepository.findById(BookObjectProvider.bookId))
			.willReturn(Optional.of(book));

		// when
		Book findBook = defaultBookService.getById(BookObjectProvider.bookId);

		// then
		verify(bookRepository).findById(BookObjectProvider.bookId);
		assertEquals(book, findBook);
	}

	@DisplayName("findById - bookId로 조회에 성공한다.")
	@Test
	void findById() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		given(bookRepository.findById(BookObjectProvider.bookId))
			.willReturn(Optional.of(book));

		// when
		Optional<Book> findBook = defaultBookService.findById(BookObjectProvider.bookId);

		// then
		verify(bookRepository).findById(BookObjectProvider.bookId);
		assertTrue(findBook.isPresent());
		assertEquals(book, findBook.get());
	}

	@DisplayName("findByIsbn - isbn으로 조회에 성공한다.")
	@Test
	void findByIsbn() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		given(bookRepository.findById(BookObjectProvider.bookId))
			.willReturn(Optional.of(book));

		// when
		Optional<Book> findBook = defaultBookService.findById(BookObjectProvider.bookId);

		// then
		verify(bookRepository).findById(BookObjectProvider.bookId);
		assertTrue(findBook.isPresent());
		assertEquals(book, findBook.get());
	}

	@DisplayName("findDetailById - id로 조회에 성공한다.")
	@Test
	void findDetailById() {
		// given
		Book book = BookObjectProvider.createRequiredFieldBook();
		BookResponse expectedResponse = BookObjectProvider.createBookResponse();
		given(bookRepository.findById(BookObjectProvider.bookId))
			.willReturn(Optional.of(book));
		given(bookMapper.entityToBookResponse(book))
			.willReturn(expectedResponse);

		// when
		BookResponse actualResponse = defaultBookService.findDetailById(BookObjectProvider.bookId);

		// then
		verify(bookRepository).findById(BookObjectProvider.bookId);
		verify(bookMapper).entityToBookResponse(book);
		assertEquals(expectedResponse, actualResponse);
	}

	@DisplayName("findAllByKeyword - 키워드로 검색하는데 성공한다.")
	@Test
	void findAllByKeyword() {
		// given
		String keyword = "test";

		SearchBookResponse searchBookResponse = new SearchBookResponse(
			BookObjectProvider.title,
			BookObjectProvider.author,
			BookObjectProvider.isbn,
			BookObjectProvider.contents,
			BookObjectProvider.url,
			BookObjectProvider.imageUrl,
			BookObjectProvider.apiProvider,
			BookObjectProvider.publisher
		);

		List<SearchBookResponse> expectedResponses = Collections.singletonList(searchBookResponse);

		BookResponses bookResponses =  new BookResponses(1,10, true, 1, 1, expectedResponses);
		given(externalBookApiOperations.searchBooks(keyword, 1, 10, SortingPolicy.ACCURACY.getName()))
			.willReturn(bookResponses);

		// when
		BookResponses actualResponses = defaultBookService.findAllByKeyword(new BookSearchRequest(null, null, keyword));

		// then
		verify(externalBookApiOperations).searchBooks(keyword, 1, 10, SortingPolicy.ACCURACY.getName());
		assertEquals(expectedResponses.get(0).isbn(), actualResponses.searchBookResponseList().get(0).isbn());
		assertEquals(expectedResponses.get(0).title(), actualResponses.searchBookResponseList().get(0).title());
		assertEquals(expectedResponses.get(0).contents(), actualResponses.searchBookResponseList().get(0).contents());
		assertEquals(expectedResponses.get(0).url(), actualResponses.searchBookResponseList().get(0).url());
	}

	@DisplayName("findAllByKeyword - 키워드가 비어있거나 알파벳과 숫자, 공백만 포함하지 않는 경우 빈 결과를 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "!@#$", "키워드에@"})
	void findAllByKeyword_WithInvalidKeyword_ReturnsEmptyList(String keyword) {
		// when
		BookResponses actualResponses = defaultBookService.findAllByKeyword(new BookSearchRequest(null, null, keyword));

		// then
		assertTrue(actualResponses.searchBookResponseList().isEmpty());
	}

	@DisplayName("findAllByKeyword - 호출 횟수 초과 시")
	@Test
	void findAllByKeyword_ApiLimitExceeded() throws JsonProcessingException {
		// given
		String keyword = "test";
		String result = "{\"code\":-10,\"msg\":\"API limit has been exceeded.\"}";
		JsonNode jsonNode = new ObjectMapper().readTree(result);
		ExternalApiError externalApiError = new ExternalApiError(-10,"API limit has been exceeded.");
		given(externalBookApiOperations.searchBooks(keyword, 1, 10, SortingPolicy.ACCURACY.getName()))
			.willThrow(new BookApiNotAvailableException(ErrorCode.KAKAO_QUOTA_EXCEEDED));


		// when, then
		assertThrows(BookApiNotAvailableException.class,
			() -> defaultBookService.findAllByKeyword(new BookSearchRequest(null, null, keyword)));
	}
}