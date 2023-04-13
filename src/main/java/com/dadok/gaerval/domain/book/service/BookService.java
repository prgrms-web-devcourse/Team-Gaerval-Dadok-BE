package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.book.entity.Book;

public interface BookService {

	BookResponses findAllByKeyword(BookSearchRequest bookSearchRequest, Long userId);

	Long createBookAndReturnId(BookCreateRequest bookCreateRequest);

	Book getById(Long bookId);

	Optional<Book> findById(Long bookId);

	Optional<Book> findByIsbn(String isbn);

	BookResponse findDetailById(Long bookId);

	SuggestionsBookFindResponses findSuggestionBooks(SuggestionsBookFindRequest request);

	UserByBookResponses findUserByBookId(Long bookId, Long userId);

	BookRecentSearchResponses findKeywordsByUserId(Long userId, Long limit);
}
