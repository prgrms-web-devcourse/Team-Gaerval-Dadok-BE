package com.dadok.gaerval.testutil;

import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.user.entity.User;

public class BookGroupCommentObjectProvider {
	public static final String comment1 = "미움받을 용기 좋아요";
	public static final String comment2 = "미움받을 용기 그저 그래요";
	public static final String comment3 = "미움받을 용기 저랑 안 맞아요";
	public static final Long bookCommentId = 234L;
	public static final List<BookGroupCommentResponse> mockCommentResponses = List.of(new BookGroupCommentResponse
		(bookCommentId, comment1, 234L, 234L, 234L),
		new BookGroupCommentResponse
			(bookCommentId, comment2, 234L, 234L, 234L),
		new BookGroupCommentResponse
			(bookCommentId, comment3, 234L, 234L, 234L)
		);

	public static GroupComment createSampleGroupComment(BookGroup bookGroup, User user) {
		GroupComment groupComment = GroupComment.create(comment1, bookGroup, user);
		ReflectionTestUtils.setField(groupComment, "id", bookCommentId);
		return groupComment;
	}


}
