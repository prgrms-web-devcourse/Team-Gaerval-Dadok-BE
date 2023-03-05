package com.dadok.gaerval.domain.book_group.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.book_group.entity.QBookGroup.*;
import static com.dadok.gaerval.domain.book_group.entity.QGroupComment.*;
import static com.dadok.gaerval.domain.book_group.entity.QGroupMember.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookGroupSupportImpl implements BookGroupSupport {

	private final JPAQueryFactory query;

	@Override
	public BookGroupResponses findAllBy(BookGroupSearchRequest request, Long userId) {

		Sort.Direction direction = request.sortDirection().toDirection();

		List<BookGroupResponse> groupResponses = query.select(Projections.fields(BookGroupResponse.class,
				bookGroup.id.as("bookGroupId"),
				bookGroup.title.as("title"),
				bookGroup.maxMemberCount.as("maximumMemberCount"),

				groupMember.count().as("memberCount"),
				groupComment.count().as("commentCount"),

				book.id.as("bookId"),
				book.imageUrl.as("imageUrl"),
				user.id.as("ownerId"),
				user.profileImage.as("ownerProfileUrl"),
				user.nickname.nickname.as("ownerNickname")
			))
			.from(bookGroup)
			.innerJoin(book).on(book.id.eq(bookGroup.book.id))
			.innerJoin(user).on(user.id.eq(bookGroup.ownerId))
			.leftJoin(bookGroup.groupMembers, groupMember)
			.leftJoin(bookGroup.comments, groupComment)
			.where(
				QueryDslUtil.generateCursorWhereCondition(bookGroup.id, request.groupCursorId(), direction),
				bookGroup.isPublic.isTrue(),
				generateMemberUserId(userId)
			)
			.groupBy(bookGroup.id,
				book.id,
				user.id
			)
			.orderBy(QueryDslUtil.getOrder(bookGroup.id, direction))
			.limit(request.pageSize() + 1)
			.fetch();

		Slice<BookGroupResponse> bookGroupResponses = QueryDslUtil.toSlice(groupResponses,
			PageRequest.of(0, request.pageSize(), Sort.by(direction, "id")));

		return new BookGroupResponses(bookGroupResponses);
	}

	private BooleanExpression generateMemberUserId(Long userId) {
		if (userId == null) {
			return null;
		}
		return groupMember.user.id.eq(userId);
	}

}
