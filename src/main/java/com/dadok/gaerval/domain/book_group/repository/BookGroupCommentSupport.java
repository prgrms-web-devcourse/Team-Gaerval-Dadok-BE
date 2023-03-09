package com.dadok.gaerval.domain.book_group.repository;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;

public interface BookGroupCommentSupport {
	boolean existsBy(Long id);

	BookGroupCommentResponses findAllBy(BookGroupCommentSearchRequest request, Long userId, Long groupId);

	BookGroupCommentResponse findGroupComment(Long commentId, Long userId, Long groupId);
}
