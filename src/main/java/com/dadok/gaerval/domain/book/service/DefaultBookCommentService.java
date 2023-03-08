package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.book.repository.BookCommentRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookCommentService implements BookCommentService {

	private final BookCommentRepository bookCommentRepository;

	private final UserService userService;

	private final BookService bookService;

	@Override
	public Long createBookComment(Long bookId, Long userId, BookCommentCreateRequest bookCommentCreateRequest) {
		User user = userService.getById(userId);
		Book book = bookService.getById(bookId);

		Optional<BookComment> findComment = bookCommentRepository.findByBookIdAndUserId(bookId, userId);
		if (findComment.isPresent()) {
			return findComment.map(comment -> {
				comment.changeComment(bookCommentCreateRequest.comment());
				return bookCommentRepository.save(comment).getId();
			}).orElseThrow(() -> new IllegalArgumentException("도서 리뷰 데이터를 찾는데 실패했습니다."));
		} else {
			BookComment newBookComment = BookComment.create(user, book, bookCommentCreateRequest.comment());
			return bookCommentRepository.save(newBookComment).getId();
		}
	}

	@Override
	public BookComment getById(Long bookId) {
		return bookCommentRepository.findById(bookId).orElseThrow(()-> new ResourceNotfoundException(BookComment.class));
	}

	@Override
	public Optional<BookComment> findById(Long bookId) {
		return bookCommentRepository.findById(bookId);
	}

	@Override
	public BookCommentResponses findBookComments(Long bookId, Long userId, BookCommentSearchRequest bookCommentSearchRequest) {
		return bookCommentRepository.findAllComments(bookId, userId, bookCommentSearchRequest);
	}

	@Override
	public BookCommentResponse updateBookComment(Long bookId, Long userId,
		BookCommentUpdateRequest bookCommentUpdateRequest) {
		return bookCommentRepository.updateBookComment(bookId, userId, bookCommentUpdateRequest);
	}
}
