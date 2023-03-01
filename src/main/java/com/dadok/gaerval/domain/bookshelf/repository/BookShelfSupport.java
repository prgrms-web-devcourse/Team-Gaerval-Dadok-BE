package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;

public interface BookShelfSupport {

	Optional<Bookshelf> findAllWithBooks(Long bookShelfId, BooksInBookShelfFindRequest request);
}
