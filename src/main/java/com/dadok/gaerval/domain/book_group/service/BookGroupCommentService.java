package com.dadok.gaerval.domain.book_group.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;

public interface BookGroupCommentService {

	BookGroupCommentResponses findAllBookGroupCommentsByGroup(BookGroupCommentSearchRequest request, Long userId, Long groupId);

	Long createBookGroupComment(Long bookGroupId, Long userId, BookGroupCommentCreateRequest request);

	void updateBookGroupComment(Long bookGroupId, Long userId, Long commentId,
		BookGroupCommentUpdateRequest bookGroupCommentUpdateRequest);

	GroupComment getById(Long id);

	Optional<GroupComment> findById(Long id);

	void deleteBookGroupComment(Long bookGroupId, Long userId, BookGroupCommentDeleteRequest bookGroupCommentDeleteRequest);
}
