package com.dadok.gaerval.domain.book.service;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.request.BestSellerSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SortingPolicy;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BestSellerBookResponses;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRecentSearchRepository;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfItemRepository;
import com.dadok.gaerval.global.config.externalapi.ExternalBookApiOperations;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	private static final int USER_VIEW_LIMIT = 3;
	private final ExternalBookApiOperations externalBookApiOperations;
	private final BookRepository bookRepository;
	private final BookshelfItemRepository bookshelfItemRepository;
	private final BookMapper bookMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final BookRecentSearchRepository bookRecentSearchRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public BookResponses findAllByKeyword(BookSearchRequest bookSearchRequest, Long userId) {

		if (StringUtils.isBlank(bookSearchRequest.query()) || !StringUtils.isAlphanumericSpace(
			bookSearchRequest.query())) {
			log.info("[DefaultBookService]-[findAllByKeyword] invalid keyword : {}", bookSearchRequest.query());
			return new BookResponses(bookSearchRequest.page(), bookSearchRequest.pageSize(), true, 0, 0,
				Collections.emptyList());
		}

		BookResponses bookResponses = externalBookApiOperations.searchBooks(bookSearchRequest.query(),
			bookSearchRequest.page(),
			bookSearchRequest.pageSize(), SortingPolicy.ACCURACY.getName());

		eventPublisher.publishEvent(new SaveKeywordEvent(userId, bookSearchRequest.query()));

		return bookResponses;
	}

	@Override
	public BestSellerBookResponses findAllBestSeller(BestSellerSearchRequest bestSellerSearchRequest) {
		return externalBookApiOperations.searchBestSellers(bestSellerSearchRequest);
	}

	private Book createBook(BookCreateRequest bookCreateRequest) {
		Optional<Book> existsBook = this.findByIsbn(bookCreateRequest.isbn());
		if (existsBook.isPresent()) {
			return existsBook.map(book -> {
				book.change(bookCreateRequest);
				return bookRepository.save(book);
			}).orElseThrow(() -> new IllegalArgumentException("도서 데이터를 찾는데 실패했습니다."));
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

	@Override
	public BookRecentSearchResponses findKeywordsByUserId(Long userId, Long limit) {
		return bookRecentSearchRepository.findRecentSearches(userId, limit);
	}
}
