package com.dadok.gaerval.domain.book_group.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.book_group.repository.GroupMemberRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DefaultBookGroupService implements BookGroupService {

	private final BookGroupRepository bookGroupRepository;

	private final BookService bookService;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final GroupMemberRepository groupMemberRepository;

	@Override
	public BookGroupResponses findAllBookGroups(BookGroupSearchRequest request) {
		return bookGroupRepository.findAllBy(request);
	}

	@Transactional
	@Override
	public Long createBookGroup(Long userId, BookGroupCreateRequest request) {
		User user = userService.getById(userId);
		Book book = bookService.createBook(request.book());
		BookGroup bookGroup = BookGroup.create(user.getId(), book, request.startDate(), request.endDate(),
			request.maxMemberCount(), request.title(), request.introduce(), request.hasJoinPasswd(),
			request.joinQuestion(), request.joinPasswd(), request.isPublic(), passwordEncoder);
		GroupMember.create(bookGroup, user);

		return bookGroupRepository.save(bookGroup).getId();
	}

	@Override
	public BookGroupDetailResponse findGroup(Long requestUserId, Long groupId) {
		return bookGroupRepository.findBookGroup(requestUserId, groupId);
	}

	@Override
	public Optional<BookGroup> findById(Long groupId) {
		return bookGroupRepository.findById(groupId);
	}

	@Transactional
	@Override
	public void join(Long groupId, Long userId, BookGroupJoinRequest request) {
		BookGroup bookGroup = bookGroupRepository.findByIdWithGroupMembers(groupId)
			.orElseThrow(() -> new ResourceNotfoundException(BookGroup.class));

		bookGroup.checkMemberCount();
		bookGroup.checkPasswd(request.joinPassword(), passwordEncoder);

		User user = userService.getById(userId);

		GroupMember.create(bookGroup, user);
	}

	@Override
	public BookGroupResponses findAllBookGroupsByUser(BookGroupSearchRequest request, Long userId) {
		return bookGroupRepository.findAllByUser(request, userId);
	}

}
