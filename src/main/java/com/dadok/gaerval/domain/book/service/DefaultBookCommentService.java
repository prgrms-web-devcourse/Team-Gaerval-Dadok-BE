package com.dadok.gaerval.domain.book.service;

import java.util.Objects;
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
import com.dadok.gaerval.domain.book.exception.NotMarkedBookException;
import com.dadok.gaerval.domain.book.repository.BookCommentRepository;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedCommentAuthorException;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultBookCommentService implements BookCommentService {

	private final BookCommentRepository bookCommentRepository;

	private final UserService userService;

	private final BookService bookService;

	private final BookshelfService bookshelfService;

	@Override
	public Long createBookComment(Long bookId, Long userId, BookCommentCreateRequest bookCommentCreateRequest) {
		User user = userService.getById(userId);
		Book book = bookService.getById(bookId);

		boolean existsComment = bookCommentRepository.existsByBookIdAndUserId(bookId, userId);
		boolean existsBookmark = bookshelfService.existsByUserIdAndBookId(userId, bookId);

		if (!existsBookmark) {
			throw new NotMarkedBookException(ErrorCode.INVALID_COMMENT_NOT_BOOKMARK);
		}
		if (existsComment) {
			throw new AlreadyContainBookCommentException(ErrorCode.ALREADY_CONTAIN_BOOK_COMMENT_MEMBER);
		} else {
			BookComment newBookComment = BookComment.create(user, book, bookCommentCreateRequest.comment());
			return bookCommentRepository.save(newBookComment).getId();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BookComment getById(Long bookCommentId) {
		return bookCommentRepository.findById(bookCommentId)
			.orElseThrow(() -> new ResourceNotfoundException(BookComment.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookComment> findById(Long bookCommentId) {
		return bookCommentRepository.findById(bookCommentId);
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
		boolean existsBookmark = bookshelfService.existsByUserIdAndBookId(userId, bookId);
		boolean existsComment = bookCommentRepository.existsByBookIdAndUserId(bookId, userId);

		if(existsComment) {
			bookCommentRepository.updateBookComment(bookId, userId, bookCommentUpdateRequest);
		}
		else if (!existsBookmark) {
			throw new NotMarkedBookException(ErrorCode.INVALID_COMMENT_NOT_BOOKMARK);
		}

		return bookCommentRepository.updateBookComment(bookId, userId, bookCommentUpdateRequest);
	}

	@Override
	public void deleteBookComment(Long bookId, Long userId, Long commentId) {
		BookComment bookComment = bookCommentRepository.findByBookId(bookId, commentId)
			.orElseThrow(() -> new ResourceNotfoundException(BookComment.class));
		checkCommentAuthor(bookComment, userId);
		bookCommentRepository.delete(bookComment);
	}

	void checkCommentAuthor(BookComment bookComment, Long userId) {
		if (!Objects.equals(bookComment.getUser().getId(), userId)) {
			throw new NotMatchedCommentAuthorException();
		}
	}
}
