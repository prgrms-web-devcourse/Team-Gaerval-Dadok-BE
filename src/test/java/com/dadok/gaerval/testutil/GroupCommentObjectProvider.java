package com.dadok.gaerval.testutil;

import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCommentObjectProvider {

	public static GroupComment createComment(String contents, BookGroup bookGroup, User user) {
		return GroupComment.create(contents, bookGroup, user);
	}

}
