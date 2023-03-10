package com.dadok.gaerval.domain.bookshelf.repository;

import org.springframework.data.domain.Slice;

import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

public interface BookShelfItemSupport {

	Slice<BookshelfItem> findAllInBookShelf(Long bookShelfId, BooksInBookShelfFindRequest request);

	UserByBookResponses findBookshelfItemUsersByBook(Long bookId, Long userId, int limit);

	boolean existsByBookshelfIdAndBookId(Long bookshelfId, Long bookId);
}
