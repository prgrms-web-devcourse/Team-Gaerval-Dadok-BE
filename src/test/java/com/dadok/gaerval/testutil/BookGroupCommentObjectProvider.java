package com.dadok.gaerval.testutil;

import java.time.LocalDateTime;
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
	public static final String profileImageUrl = "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg";
	public static final List<BookGroupCommentResponse> mockCommentResponses = List.of(new BookGroupCommentResponse
			(123L, comment1, 234L, 234L, 123L, profileImageUrl, LocalDateTime.now(), LocalDateTime.now(), "티나", Boolean.TRUE),
		new BookGroupCommentResponse
			(234L, comment2, 234L, 234L, 234L, profileImageUrl, LocalDateTime.now(), LocalDateTime.now(), "0Soo", Boolean.FALSE),
		new BookGroupCommentResponse
			(456L, comment3, 234L, 234L, 456L, profileImageUrl, LocalDateTime.now(), LocalDateTime.now(), "youngijang", Boolean.FALSE)
	);

	public static GroupComment createSampleGroupComment(BookGroup bookGroup, User user) {
		GroupComment groupComment = GroupComment.create(comment1, bookGroup, user);
		ReflectionTestUtils.setField(groupComment, "id", bookCommentId);
		return groupComment;
	}

}
