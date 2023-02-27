package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.entity.Book;

@Service
public class DefaultBookService implements BookService {
	@Override
	public Book createBook(BookCreateRequest bookCreateRequest) {
		return null;
	}

	@Override
	public Book getById(Long bookId) {
		return null;
	}

	@Override
	public Optional<Book> findById(Long bookId) {
		return Optional.empty();
	}

	@Override
	public Optional<Book> findByIsbn(String isbn) {
		return Optional.empty();
	}

}
