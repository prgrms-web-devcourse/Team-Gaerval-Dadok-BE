package com.dadok.gaerval.domain.book.repository;

import java.util.Optional;

import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.BookComment;

public interface BookCommentSupport {

	boolean existsBy(Long bookCommentId);

	boolean existsByBookIdAndUserId(Long bookId, Long userId);

	Optional<BookComment> findByBookId(Long bookId, Long commentId);

	BookCommentResponses findAllComments(Long bookId, Long userId, BookCommentSearchRequest bookCommentSearchRequest);

	BookCommentResponse updateBookComment(Long bookId, Long userId,
		BookCommentUpdateRequest bookCommentUpdateRequest);
}
