package com.dadok.gaerval.domain.book.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookSupportImplTest {

	private final BookRepository bookRepository;

	@DisplayName("findSuggestionBooks - 쿼리 테스트")
	@Test
	void findSuggestionBooks_success() {
		SuggestionsBookFindRequest suggestionsBookFindRequest = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			null, null);
		SuggestionsBookFindResponses suggestionBooks = bookRepository.findSuggestionBooks(suggestionsBookFindRequest);
	}

	@DisplayName("generateCursorId 테스트 - id = 999 Sort = null, PageSize = 10")
	@Test
	void generateCursorId_desc() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);

		SuggestionsBookFindRequest suggestionsBookFindRequest = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			999L, null);

		bookRepository.findSuggestionBooks(suggestionsBookFindRequest);
	}

	@DisplayName("generateCursorId 테스트 - id = 999 Sort = DESC, PageSize = 10")
	@Test
	void showgenerateCursorId_desc_nonNull() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);

		SuggestionsBookFindRequest suggestionsBookFindRequest = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			999L, SortDirection.DESC);

		bookRepository.findSuggestionBooks(suggestionsBookFindRequest);
	}

	@DisplayName("generateCursorId 테스트 - id = 999 Sort = DESC, PageSize = 10")
	@Test
	void generateCursorId_asc() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);

		SuggestionsBookFindRequest suggestionsBookFindRequest = new SuggestionsBookFindRequest(JobGroup.DEVELOPMENT, 50,
			999L, SortDirection.ASC);

		bookRepository.findSuggestionBooks(suggestionsBookFindRequest);
	}


}