package com.dadok.gaerval.domain.book.service;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.global.config.externalapi.ExternalBookApiOperations;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	private static final int USER_VIEW_LIMIT = 3;
	private final ExternalBookApiOperations externalBookApiOperations;
	private final BookRepository bookRepository;
	private final BookshelfItemRepository bookshelfItemRepository;
	private final BookMapper bookMapper;

	@Override
	@Transactional
	public BookResponses findAllByKeyword(BookSearchRequest bookSearchRequest) {

		if (StringUtils.isBlank(bookSearchRequest.query()) || !StringUtils.isAlphanumericSpace(
			bookSearchRequest.query())) {
			log.info("[DefaultBookService]-[findAllByKeyword] invalid keyword : {}", bookSearchRequest.query());
			return new BookResponses(true, 0, 0, Collections.emptyList());
		}

		return externalBookApiOperations.searchBooks(bookSearchRequest.query(), bookSearchRequest.page(),
			bookSearchRequest.pageSize(), SortingPolicy.ACCURACY.getName());
	}

	private Book createBook(BookCreateRequest bookCreateRequest) {
		Optional<Book> existsBook = this.findByIsbn(bookCreateRequest.isbn());
		if (existsBook.isPresent()) {
			return existsBook.map(book -> {
				book.change(bookCreateRequest);
				return bookRepository.save(book);
			}).orElseThrow(() -> new IllegalArgumentException("?????? ???????????? ????????? ??????????????????."));
		} else {
			Book newBook = bookMapper.createBookRequestToEntity(bookCreateRequest);
			return bookRepository.save(newBook);
		}
	}

	@Override
	@Transactional
	public Long createBookAndReturnId(BookCreateRequest bookCreateRequest) {
		return this.createBook(bookCreateRequest).getId();
	}

	@Override
	public Book getById(Long bookId) {
		return bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotfoundException(Book.class));
	}

	@Override
	public Optional<Book> findById(Long bookId) {
		return bookRepository.findById(bookId);
	}

	@Override
	public Optional<Book> findByIsbn(String isbn) {
		return bookRepository.findBookByIsbn(isbn);
	}

	@Override
	public BookResponse findDetailById(Long bookId) {
		return bookMapper.entityToBookResponse(this.getById(bookId));
	}

	@Override
	public SuggestionsBookFindResponses findSuggestionBooks(SuggestionsBookFindRequest request) {
		return bookRepository.findSuggestionBooks(request);
	}

	@Override
	public UserByBookResponses findUserByBookId(Long bookId, Long userId) {
		return bookshelfItemRepository.findBookshelfItemUsersByBook(bookId, userId, USER_VIEW_LIMIT);
	}

}
