package com.dadok.gaerval.domain.book.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.book.entity.QBookComment.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.dadok.gaerval.global.util.QueryDslUtil.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.request.BookCommentUpdateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponse;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.book.entity.QBookComment;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookCommentSupportImpl implements BookCommentSupport {

	private final JPAQueryFactory queryFactory;

	@Override
	public BookCommentResponses findAllComments(Long bookId, Long userId,
		BookCommentSearchRequest bookCommentSearchRequest) {
		Sort.Direction direction = bookCommentSearchRequest.sortDirection().toDirection();

		List<BookCommentResponse> bookCommentResponses = queryFactory.select(
				Projections.constructor(BookCommentResponse.class,
					bookComment.id.as("commentId"),
					bookComment.comment.as("contents"),
					bookComment.book.id.as("bookId"),
					user.id.as("userId"),
					user.profileImage.as("userProfileImage"),
					bookComment.createdAt.as("createdAt"),
					bookComment.modifiedAt.as("modifiedAt"),
					user.nickname.nickname.as("nickname"),
					generateNullableBooleanExpression(user.id, userId).as("writtenByCurrentUser")
				))
			.from(bookComment)
			.innerJoin(user).on(user.id.eq(bookComment.user.id))
			.innerJoin(book).on(book.id.eq(bookComment.book.id))
			.where(
				QueryDslUtil.generateCursorWhereCondition(bookComment.id,
					bookCommentSearchRequest.bookCommentCursorId(), direction),
				QueryDslUtil.generateIdWhereCondition(book.id, bookId)
			)
			.orderBy(QueryDslUtil.getOrder(bookComment.id, direction))
			.limit(bookCommentSearchRequest.pageSize() + 1)
			.fetch();

		Slice<BookCommentResponse> bookCommentResponseSlice = QueryDslUtil.toSlice(bookCommentResponses,
			PageRequest.of(0, bookCommentSearchRequest.pageSize(), Sort.by(direction, "id")));

		return new BookCommentResponses(bookCommentResponseSlice);
	}

	@Override
	public boolean existsBy(Long bookCommentId) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(bookComment)
			.where(bookComment.id.eq(bookCommentId))
			.fetchFirst();

		return fetchOne != null;
	}

	@Override
	public boolean existsByBookIdAndUserId(Long bookId, Long userId) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(bookComment)
			.where(bookComment.user.id.eq(userId),
				bookComment.book.id.eq(bookId)
			)
			.fetchFirst();

		return fetchOne != null;
	}

	@Override
	public Optional<BookComment> findByBookId(Long bookId, Long commentId) {
		BookComment bookComment = queryFactory
			.selectFrom(QBookComment.bookComment)
			.where(
				QBookComment.bookComment.book.id.eq(bookId),
				QBookComment.bookComment.id.eq(commentId)
			)
			.fetchFirst();
		return Optional.ofNullable(bookComment);
	}

	@Override
	public BookCommentResponse updateBookComment(Long bookId, Long userId,
		BookCommentUpdateRequest bookCommentUpdateRequest) {

		BooleanExpression commentBelongsToUser = bookComment.id.eq(bookCommentUpdateRequest.commentId())
			.and(bookComment.user.id.eq(userId));

		long updatedRows = queryFactory.update(bookComment)
			.set(bookComment.comment, bookCommentUpdateRequest.comment())
			.where(commentBelongsToUser)
			.execute();

		if (updatedRows == 0) {
			throw new ResourceNotfoundException(BookCommentResponse.class);
		}

		return queryFactory.select(
				Projections.constructor(BookCommentResponse.class,
					bookComment.id.as("commentId"),
					bookComment.comment.as("contents"),
					bookComment.book.id.as("bookId"),
					user.id.as("userId"),
					user.profileImage.as("userProfileImage"),
					bookComment.createdAt.as("createdAt"),
					bookComment.modifiedAt.as("modifiedAt"),
					user.nickname.nickname.as("nickname"),
					Expressions.booleanTemplate("{0} = {1}", user.id, userId).as("writtenByCurrentUser")
				))
			.from(bookComment)
			.innerJoin(user).on(user.id.eq(bookComment.user.id))
			.innerJoin(book).on(book.id.eq(bookComment.book.id))
			.where(book.id.eq(bookId))
			.orderBy(bookComment.createdAt.asc())
			.fetchFirst();
	}
}
