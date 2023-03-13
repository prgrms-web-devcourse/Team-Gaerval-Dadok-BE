package com.dadok.gaerval.domain.book_group.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupJoinRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupUpdateRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.exception.CannotLeaveGroupOwnerException;
import com.dadok.gaerval.domain.book_group.exception.NotContainBookGroupException;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.book_group.repository.GroupMemberRepository;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.TimeHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookGroupService implements BookGroupService {

	private final BookGroupRepository bookGroupRepository;

	private final GroupMemberRepository groupMemberRepository;

	private final BookService bookService;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final BookshelfService bookshelfService;

	private final TimeHolder timeHolder;

	@Override
	@Transactional(readOnly = true)
	public BookGroupResponses findAllBookGroups(BookGroupSearchRequest request) {
		return bookGroupRepository.findAllBy(request);
	}

	@Transactional
	@Override
	public Long createBookGroup(Long userId, BookGroupCreateRequest request) {
		User user = userService.getById(userId);
		Book book = bookService.findById(request.bookId()).orElseThrow(() -> new ResourceNotfoundException(Book.class));
		BookGroup bookGroup = BookGroup.create(user.getId(), book, request.startDate(), request.endDate(),
			request.maxMemberCount(), request.title(), request.introduce(), request.hasJoinPasswd(),
			request.joinQuestion(), request.joinPasswd(), request.isPublic(), passwordEncoder, timeHolder);
		GroupMember.create(bookGroup, user, timeHolder);
		bookGroup = bookGroupRepository.save(bookGroup);
		bookshelfService.insertIfNotPresent(user.getId(), book.getId());
		return bookGroup.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public BookGroupDetailResponse findGroup(Long requestUserId, Long groupId) {
		return bookGroupRepository.findBookGroup(requestUserId, groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookGroup> findById(Long groupId) {
		return bookGroupRepository.findById(groupId);
	}

	@Transactional
	@Override
	public void join(Long groupId, Long userId, BookGroupJoinRequest request) {
		BookGroup bookGroup = bookGroupRepository.findByIdWithGroupMembersForUpdate(groupId)
			.orElseThrow(() -> new ResourceNotfoundException(BookGroup.class));

		bookGroup.checkPasswd(request.joinPasswd(), passwordEncoder);
		User user = userService.getById(userId);

		GroupMember groupMember = GroupMember.create(user);
		bookGroup.addMember(groupMember, timeHolder);

		bookshelfService.insertIfNotPresent(user.getId(), bookGroup.getBook().getId());
	}

	@Transactional
	@Override
	public void deleteBookGroup(Long groupId, Long userId) {
		BookGroup bookGroup = bookGroupRepository.findById(groupId)
			.orElseThrow(() -> new ResourceNotfoundException(BookGroup.class));
		bookGroup.validateDelete(userId);
		bookGroupRepository.deleteById(groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public BookGroup getById(Long id) {
		return bookGroupRepository.findById(id).orElseThrow(() -> new ResourceNotfoundException(BookGroup.class));
	}

	@Override
	@Transactional
	public void updateBookGroup(Long groupId, Long userId, BookGroupUpdateRequest request) {
		BookGroup bookGroup = bookGroupRepository.findById(groupId)
			.orElseThrow(() -> new ResourceNotfoundException(BookGroup.class));
		bookGroup.validateOwner(userId);
		bookGroup.changeBookGroupContents(request.title(), request.introduce(), request.endDate(),
			request.maxMemberCount(), timeHolder);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean checkGroupMember(Long userId, Long bookGroupId) {
		return groupMemberRepository.existsByBookGroupIdAndUserId(bookGroupId, userId);
	}

	@Override
	@Transactional
	public void leave(Long groupId, Long userId) {
		BookGroup bookGroup = this.getById(groupId);
		if (Objects.equals(bookGroup.getOwnerId(), userId)) {
			throw new CannotLeaveGroupOwnerException();
		}
		GroupMember groupMember = groupMemberRepository.findByBookGroupIdAndUserId(groupId, userId)
			.orElseThrow(NotContainBookGroupException::new);
		groupMemberRepository.delete(groupMember);
	}

	@Override
	@Transactional(readOnly = true)
	public BookGroupResponses findAllBookGroupsByUser(BookGroupSearchRequest request, Long userId) {
		return bookGroupRepository.findAllByUser(request, userId);
	}

}
