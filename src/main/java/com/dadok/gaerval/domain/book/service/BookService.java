package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;

public interface BookService {
	//
	// BookResponses findAllByKeyword(String keyword, Pageable pageable, );

	Book createBook(BookCreateRequest bookCreateRequest);

	Book getById(Long bookId);

	Optional<Book> findById(Long bookId);

	Optional<Book> findByIsbn(String isbn);

	// BookResponse findDetailById(Long bookId);
	//
	// PopularBookOfJobResponses findAllPopularBook(String jobGroup, Pageable pageable);
}
