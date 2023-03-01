package com.dadok.gaerval.domain.bookshelf.repository;

import org.springframework.data.domain.Slice;

import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

public interface BookShelfItemSupport {

	Slice<BookshelfItem> findAllInBookShelf(Long bookShelfId, BooksInBookShelfFindRequest request);

}
