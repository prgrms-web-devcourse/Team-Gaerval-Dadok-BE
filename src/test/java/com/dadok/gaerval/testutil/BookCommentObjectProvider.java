package com.dadok.gaerval.testutil;

import java.time.LocalDateTime;
import java.util.List;

import com.dadok.gaerval.domain.book.dto.request.BookCommentCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.user.entity.User;

public class BookCommentObjectProvider {
	public static final Long bookId = 9L;
	public static final Long userId = 1L;

	public static final Long commentId1 = 1L;
	public static final Long commentId2 = 2L;
	public static final Long commentId3 = 3L;

	public static final String comment1 = "백점 만점";
	public static final String comment2 = "백점 만점에 오십점";
	public static final String comment3 = "백점 만점에 빵점";

	public static BookComment create1(User user, Book book) {
		return BookComment.create(user, book, comment1);
	}

	public static BookComment create2(User user, Book book) {
		return BookComment.create(user, book, comment2);
	}

	public static BookComment create3(User user, Book book) {
		return BookComment.create(user, book, comment3);
	}

	public static BookCommentCreateRequest createBookCommentCreateRequest() {
		return new BookCommentCreateRequest(comment1);
	}

	public static BookCommentUpdateRequest createCommentUpdateRequest() {
		return new BookCommentUpdateRequest(commentId1, comment1);
	}

	public static List<BookCommentResponse> createMockResponses() {
		return List.of(
			new BookCommentResponse(commentId1, comment1, 1L, 1L, UserObjectProvider.PICTURE_URL,
				LocalDateTime.now(), LocalDateTime.now(), "티나", true),
			new BookCommentResponse(commentId1, comment2, 1L, 2L, UserObjectProvider.PICTURE_URL,
				LocalDateTime.now(), LocalDateTime.now(), "0soo", false),
			new BookCommentResponse(commentId1, comment3, 1L, 3L, UserObjectProvider.PICTURE_URL,
				LocalDateTime.now(), LocalDateTime.now(), "foreverYoung", false)
		);
	}
}
