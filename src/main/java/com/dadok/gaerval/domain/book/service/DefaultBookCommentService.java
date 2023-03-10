package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.book.exception.AlreadyContainBookCommentException;
import com.dadok.gaerval.domain.book.repository.BookCommentRepository;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentDeleteRequest;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultBookCommentService implements BookCommentService {

	private final BookCommentRepository bookCommentRepository;

	private final UserService userService;

	private final BookService bookService;

	@Override
	public Long createBookComment(Long bookId, Long userId, BookCommentCreateRequest bookCommentCreateRequest) {
		User user = userService.getById(userId);
		Book book = bookService.getById(bookId);

		boolean existsComment = bookCommentRepository.existsByBookIdAndUserId(bookId, userId);
		if (existsComment) {
			throw new AlreadyContainBookCommentException();
		} else {
			BookComment newBookComment = BookComment.create(user, book, bookCommentCreateRequest.comment());
			return bookCommentRepository.save(newBookComment).getId();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BookComment getById(Long bookId) {
		return bookCommentRepository.findById(bookId)
			.orElseThrow(() -> new ResourceNotfoundException(BookComment.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookComment> findById(Long bookId) {
		return bookCommentRepository.findById(bookId);
	}

	@Override
	@Transactional(readOnly = true)
	public BookCommentResponses findBookCommentsBy(Long bookId, Long userId,
		BookCommentSearchRequest bookCommentSearchRequest) {
		return bookCommentRepository.findAllComments(bookId, userId, bookCommentSearchRequest);
	}

	@Override
	public BookCommentResponse updateBookComment(Long bookId, Long userId,
		BookCommentUpdateRequest bookCommentUpdateRequest) {
		return bookCommentRepository.updateBookComment(bookId, userId, bookCommentUpdateRequest);
	}

	@Override
	public void deleteBookComment(Long bookId, Long userId,
		BookGroupCommentDeleteRequest bookGroupCommentDeleteRequest) {
		Optional<BookComment> existsComment = bookCommentRepository.findByBookIdAndUserId(bookId, userId);
		Long byId = this.getById(bookGroupCommentDeleteRequest.commentId()).getId();
		bookCommentRepository.delete(existsComment.filter(c -> c.getId().equals(byId))
			.orElseThrow(() -> new ResourceNotfoundException(BookComment.class)));
	}
}
