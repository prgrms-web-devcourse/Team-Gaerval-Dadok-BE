package com.dadok.gaerval.domain.book_group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.service.BookService;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookGroupService implements BookGroupService {

	private final BookGroupRepository bookGroupRepository;

	private final BookService bookService;

	private final UserService userService;

	@Transactional(readOnly = true)
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
			request.maxMemberCount(), request.title(), request.introduce(), request.isPublic());
		GroupMember.create(bookGroup, user);
		return bookGroupRepository.save(bookGroup).getId();
	}

}
