package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.repository.CustomDataJpaTest;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookShelfItemSupportImplTest {

	private final BookshelfItemRepository bookshelfItemRepository;

	@DisplayName("findAllWithBooks - id = null, Type = Read, Sort = DESC, PageSize = 10")
	@Test
	void findAllWithBooks_() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, null, SortDirection.DESC);
		bookshelfItemRepository.findAllInBookShelf(null, booksInBookShelfFindRequest);
	}

	@DisplayName("findAllWithBooks - id = 50L, Type = Read, Sort = DESC, PageSize = 10")
	@Test
	void findAllWithBooks_withId() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, 50L, SortDirection.DESC);
		bookshelfItemRepository.findAllInBookShelf(50L, booksInBookShelfFindRequest);
	}

	@DisplayName("findAllWithBooks - id = 50L, Type = null Sort = DESC, PageSize = 10")
	@Test
	void findAllWithBooks_typeNull() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			null, 10, 50L, SortDirection.DESC);
		bookshelfItemRepository.findAllInBookShelf(50L, booksInBookShelfFindRequest);
	}

	@DisplayName("findAllWithBooks - id = 50L, Type = Read, Sort = ASC, PageSize = 10")
	@Test
	void findAllWithBooks_sortASC() {
		BooksInBookShelfFindRequest booksInBookShelfFindRequest = new BooksInBookShelfFindRequest(
			BookshelfItemType.READ, 10, 50L, SortDirection.ASC);
		bookshelfItemRepository.findAllInBookShelf(50L, booksInBookShelfFindRequest);
	}
}