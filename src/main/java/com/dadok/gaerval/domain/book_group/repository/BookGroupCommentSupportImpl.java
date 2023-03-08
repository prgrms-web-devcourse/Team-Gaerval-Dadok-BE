package com.dadok.gaerval.domain.book_group.repository;

import static com.dadok.gaerval.domain.book_group.entity.QBookGroup.*;
import static com.dadok.gaerval.domain.book_group.entity.QGroupComment.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookGroupCommentSupportImpl implements BookGroupCommentSupport {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsBy(Long id) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(groupComment)
			.where(groupComment.id.eq(id))
			.fetchFirst();

		return fetchOne != null;
	}

	@Override
	public BookGroupCommentResponses findAllBy(BookGroupCommentSearchRequest request, Long userId, Long groupId) {
		Sort.Direction direction = request.sortDirection().toDirection();

		List<BookGroupCommentResponse> groupCommentResponses = queryFactory.select(
				constructor(BookGroupCommentResponse.class,
					groupComment.id.as("commentId"),
					groupComment.contents.as("contents"),
					bookGroup.id.as("booGroupId"),
					groupComment.parentComment.id.as("parentCommentId"),
					user.id.as("userId"),
					user.profileImage.as("userProfileImage"),
					user.nickname.as("nickname"),
					groupComment.createdAt.as("createdAt"),
					groupComment.modifiedAt.as("modifiedAt"),
					Expressions.booleanTemplate("{0} = {1}", user.id, userId).as("writtenByCurrentUser")
				))
			.from(groupComment)
			.innerJoin(user).on(user.id.eq(groupComment.user.id))
			.innerJoin(bookGroup).on(bookGroup.id.eq(groupComment.bookGroup.id))
			.where(
				QueryDslUtil.generateCursorWhereCondition(groupComment.id, request.groupCommentCursorId(), direction),
				bookGroup.id.eq(groupId)
			)
			.orderBy(QueryDslUtil.getOrder(groupComment.id, direction))
			.limit(request.pageSize() + 1)
			.fetch();

		Slice<BookGroupCommentResponse> bookGroupResponses = QueryDslUtil.toSlice(groupCommentResponses,
			PageRequest.of(0, request.pageSize(), Sort.by(direction, "id")));

		return new BookGroupCommentResponses(bookGroupResponses);
	}
}
