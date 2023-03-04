package com.dadok.gaerval.domain.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.dadok.gaerval.testutil.BookObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookServiceSliceTest {

	@InjectMocks
	private DefaultBookService defaultBookService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookMapper bookMapper;

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
		Book savedBook = defaultBookService.createBook(bookCreateRequest);

		// then
		verify(bookRepository).save(any());
		assertEquals(book.getId(), savedBook.getId());
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

	@DisplayName("findSuggestionBooks - JobGroup으로 가장 책장에 많이 꽂힌 책 순으로 가져온다.")
	@Test
	void findSuggestionBooks_success() {
		//given
		SuggestionsBookFindRequest request = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			null, null);

		List<SuggestionsBookFindResponse> suggestionsBookFindResponses = List.of(
			new SuggestionsBookFindResponse(1L, "http://imageurl4.com"
				, "jpa", "김영한", "123456789", "영진", "http://이미지링크4.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 10L),
			new SuggestionsBookFindResponse(99L, "http://imageurl5.com"
				, "에스큐엘정복", "김도강", "123456789", "영풍", "http://이미지링크5.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 8L),
			new SuggestionsBookFindResponse(100L, "http://imageurl7.com"
				, "나는 왜이렇게 귀여울까", "김별", "123456789", "교보", "http://이미지링크2.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(),5L),
			new SuggestionsBookFindResponse(10L, "http://imageurl4.com"
				, "세상에서 김별이 제일 귀엽다", "강형욱", "123456789", "오렐리", "http://이미지링크3.com",
				JobGroup.DEVELOPMENT.getGroupName(),
				JobGroup.JobName.BACKEND_DEVELOPER.getJobName(), 1L)
		);

		Slice<SuggestionsBookFindResponse> bookFindResponses = QueryDslUtil.toSlice(suggestionsBookFindResponses,
			PageRequest.of(0, 50, Sort.by(
				Sort.Direction.DESC, "book_id"
			)));

		SuggestionsBookFindResponses responses = new SuggestionsBookFindResponses(bookFindResponses,
			JobGroup.DEVELOPMENT);

		given(bookRepository.findSuggestionBooks(request))
			.willReturn(responses);

		//when
		SuggestionsBookFindResponses suggestionBooks = defaultBookService.findSuggestionBooks(request);

		//then
		assertThat(suggestionBooks)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", 4)
			.hasFieldOrPropertyWithValue("isEmpty", false);

		assertThat(suggestionBooks.books()).isSortedAccordingTo((o1, o2) -> o2.count().compareTo(o1.count()));
	}

	@DisplayName("findSuggestionBooks - JobGroup으로 조회했지만, 비어있어서 빈 응답이 온다.")
	@Test
	void findSuggestionBooks_empty() {
		//given
		SuggestionsBookFindRequest request = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			null, null);

		List<SuggestionsBookFindResponse> suggestionsBookFindResponses = Collections.emptyList();

		Slice<SuggestionsBookFindResponse> bookFindResponses = QueryDslUtil.toSlice(suggestionsBookFindResponses,
			PageRequest.of(0, 50, Sort.by(
				Sort.Direction.DESC, "book_id"
			)));

		SuggestionsBookFindResponses responses = new SuggestionsBookFindResponses(bookFindResponses,
			JobGroup.DEVELOPMENT);

		given(bookRepository.findSuggestionBooks(request))
			.willReturn(responses);

		//when
		SuggestionsBookFindResponses suggestionBooks = defaultBookService.findSuggestionBooks(request);

		//then
		assertThat(suggestionBooks)
			.hasFieldOrPropertyWithValue("isFirst", true)
			.hasFieldOrPropertyWithValue("isLast", true)
			.hasFieldOrPropertyWithValue("hasNext", false)
			.hasFieldOrPropertyWithValue("count", 0)
			.hasFieldOrPropertyWithValue("isEmpty", true)
			.hasFieldOrPropertyWithValue("books", Collections.emptyList());
	}
}