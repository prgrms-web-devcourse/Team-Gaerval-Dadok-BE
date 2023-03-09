package com.dadok.gaerval.domain.book_group.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.book_group.exception.NotContainBookGroupException;
import com.dadok.gaerval.domain.book_group.repository.BookGroupCommentRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookGroupCommentService implements BookGroupCommentService {

	private final BookGroupCommentRepository bookGroupCommentRepository;

	private final UserService userService;

	private final BookGroupService bookGroupService;

	@Override
	@Transactional(readOnly = true)
	public BookGroupCommentResponses findAllBookGroupCommentsByGroup(BookGroupCommentSearchRequest request, Long userId,
		Long groupId) {
		return bookGroupCommentRepository.findAllBy(request, userId, groupId);
	}

	@Override
	@Transactional
	public Long createBookGroupComment(Long groupId, Long userId, BookGroupCommentCreateRequest request) {
		User user = userService.getById(userId);
		BookGroup bookGroup = bookGroupService.getById(groupId);
		if (!bookGroupService.checkGroupMember(userId, bookGroup.getId())) {
			throw new NotContainBookGroupException();
		}
		GroupComment groupComment = GroupComment.create(
			request.comment(),
			bookGroup,
			user
		);

		// 부모가 없는 1단계 댓글
		if (request.parentCommentId() == null) {
			GroupComment savedGroupComment = bookGroupCommentRepository.save(groupComment);
			return savedGroupComment.getId();
		} else {
			// 부모가 있는 2단계 댓글
			if (!bookGroupCommentRepository.existsBy(request.parentCommentId())) {
				throw new ResourceNotfoundException(GroupComment.class);
			} else {
				GroupComment parentComment = this.getById(request.parentCommentId());
				groupComment.addParent(parentComment);
				GroupComment savedGroupComment = bookGroupCommentRepository.save(groupComment);
				return savedGroupComment.getId();
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public GroupComment getById(Long id) {
		return bookGroupCommentRepository.findById(id)
			.orElseThrow(() -> new ResourceNotfoundException(GroupComment.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GroupComment> findById(Long id) {
		return bookGroupCommentRepository.findById(id);
	}
}
