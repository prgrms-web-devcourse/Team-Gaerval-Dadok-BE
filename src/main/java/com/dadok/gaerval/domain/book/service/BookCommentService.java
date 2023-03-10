package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;

public interface BookCommentService {

	Long createBookComment(Long bookId, Long userId, BookCommentCreateRequest bookCommentCreateRequest);

	BookComment getById(Long bookId);

	Optional<BookComment> findById(Long bookId);

	BookCommentResponses findBookCommentsBy(Long bookId, Long userId, BookCommentSearchRequest bookCommentSearchRequest);

	BookCommentResponse updateBookComment(Long bookId, Long userId, BookCommentUpdateRequest bookCommentUpdateRequest);

	void deleteBookComment(Long bookId, Long userId, BookGroupCommentDeleteRequest bookGroupCommentDeleteRequest);
}
