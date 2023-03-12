package com.dadok.gaerval.domain.book_group.repository;

import static com.dadok.gaerval.domain.book_group.entity.QBookGroup.*;
import static com.dadok.gaerval.domain.book_group.entity.QGroupComment.*;
import static com.dadok.gaerval.domain.book_group.entity.QGroupMember.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupCommentResponses;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.Tuple;
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

		List<Tuple> groupCommentTuples = queryFactory
			.select(
				groupComment.id,
				groupComment.contents,
				bookGroup.id,
				groupComment.parentComment.id,
				user.id,
				user.profileImage,
				groupComment.createdAt,
				groupComment.modifiedAt,
				user.nickname.nickname,
				bookGroup.isPublic
			)
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

		List<BookGroupCommentResponse> bookGroupCommentResponses = groupCommentTuples.stream().map(
			tuple -> new BookGroupCommentResponse(
				tuple.get(groupComment.id),
				tuple.get(groupComment.contents),
				tuple.get(bookGroup.id),
				tuple.get(groupComment.parentComment.id),
				tuple.get(user.id),
				tuple.get(user.profileImage),
				tuple.get(groupComment.createdAt),
				tuple.get(groupComment.modifiedAt),
				tuple.get(user.nickname.nickname),
				Objects.equals(tuple.get(user.id), userId))
		).toList();

		Boolean isPublic = null;
		Boolean isGroupMember = null;

		if (!groupCommentTuples.isEmpty()) {
			Tuple tuple = groupCommentTuples.get(0);
			isPublic = tuple.get(bookGroup.isPublic);

			isGroupMember =
				userId != null && queryFactory.selectOne()
					.from(groupMember)
					.where(groupMember.bookGroup.id.eq(groupId),
						groupMember.user.id.eq(userId)
					)
					.fetchFirst() != null;
		}

		Slice<BookGroupCommentResponse> bookGroupResponses = QueryDslUtil.toSlice(bookGroupCommentResponses,
			PageRequest.of(0, request.pageSize(), Sort.by(direction, "id")));

		return new BookGroupCommentResponses(isPublic, isGroupMember, bookGroupResponses);
	}

	@Override
	public BookGroupCommentResponse findGroupComment(Long commentId, Long userId, Long groupId) {
		return queryFactory.select(
				constructor(BookGroupCommentResponse.class,
					groupComment.id.as("commentId"),
					groupComment.contents.as("contents"),
					bookGroup.id.as("booGroupId"),
					groupComment.parentComment.id.as("parentCommentId"),
					user.id.as("userId"),
					user.profileImage.as("userProfileImage"),
					groupComment.createdAt.as("createdAt"),
					groupComment.modifiedAt.as("modifiedAt"),
					user.nickname.nickname.as("nickname"),
					Expressions.booleanTemplate("{0} = {1}", user.id, userId).as("writtenByCurrentUser")
				))
			.from(groupComment)
			.innerJoin(user).on(user.id.eq(groupComment.user.id))
			.innerJoin(bookGroup).on(bookGroup.id.eq(groupComment.bookGroup.id))
			.where(
				bookGroup.id.eq(groupId),
				groupComment.id.eq(commentId)
			).fetchFirst();
	}
}
