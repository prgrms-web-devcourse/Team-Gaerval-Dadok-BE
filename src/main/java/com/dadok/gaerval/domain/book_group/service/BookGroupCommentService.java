package com.dadok.gaerval.domain.book_group.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;

public interface BookGroupCommentService {

	BookGroupCommentResponses findAllBookGroupCommentsByGroup(BookGroupCommentSearchRequest request, Long userId, Long groupId);

	Long createBookGroupComment(Long bookGroupId, Long userId, BookGroupCommentCreateRequest request);

	GroupComment getById(Long id);

	Optional<GroupComment> findById(Long id);
}
