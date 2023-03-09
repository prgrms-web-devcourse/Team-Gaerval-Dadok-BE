package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelf.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;
import static com.querydsl.core.types.Projections.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book.dto.response.UserByBookResponse;
import com.dadok.gaerval.domain.book.dto.response.UserByBookResponses;
import com.dadok.gaerval.domain.bookshelf.dto.request.BooksInBookShelfFindRequest;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItemType;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookShelfItemSupportImpl implements BookShelfItemSupport {

	private final JPAQueryFactory query;

	@Override
	public Slice<BookshelfItem> findAllInBookShelf(Long bookShelfId, BooksInBookShelfFindRequest request) {

		Sort.Direction direction = request.getSortDirection().toDirection();

		List<BookshelfItem> bookshelfItems = query.selectFrom(bookshelfItem)
			.leftJoin(bookshelfItem.book, book)
			.fetchJoin()
			.where(
				QueryDslUtil.generateCursorWhereCondition(book.id,
					request.getBookCursorId(), direction),
				bookShelfItemType(request.getType())
			)
			.limit(request.getPageSize() + 1)
			.orderBy(order(direction))
			.fetch();

		return QueryDslUtil.toSlice(bookshelfItems, PageRequest.of(0, request.getPageSize(),
			Sort.by(direction, "id")));
	}

	@Override
	public UserByBookResponses findBookshelfItemUsersByBook(Long bookId, Long userId, int limit) {
		List<UserByBookResponse> userByBookResponses = query
			.select(constructor(UserByBookResponse.class,
				user.id,
				user.profileImage
			))
			.from(bookshelfItem)
			.innerJoin(bookshelfItem.book, book)
			.innerJoin(bookshelfItem.bookshelf, bookshelf)
			.innerJoin(bookshelf.user, user)
			.where(bookshelfItem.book.id.eq(bookId))
			.orderBy(bookshelfItem.createdAt.desc())
			.fetch();

		if (userByBookResponses.isEmpty()) {
			return new UserByBookResponses(bookId, 0, false, new ArrayList<>());
		}

		boolean isInMyBookshelf =
			userId != null && query.selectOne()
				.from(bookshelfItem)
				.where(bookshelfItem.book.id.eq(bookId),
					bookshelfItem.bookshelf.user.id.eq(userId)
				)
				.fetchFirst() != null;

		return new UserByBookResponses(bookId, userByBookResponses.size(), isInMyBookshelf,
			userByBookResponses.stream().limit(limit).collect(Collectors.toList()));
	}

	private OrderSpecifier<?> order(Sort.Direction direction) {

		return switch (direction) {
			case DESC -> bookshelfItem.id.desc();
			default -> bookshelfItem.id.asc();
		};
	}

	private BooleanExpression bookShelfItemType(BookshelfItemType type) {
		return type == null ? null : bookshelfItem.type.eq(type);
	}

	private BooleanExpression generateCursorId(Long cursorId, Sort.Direction direction) {
		if (cursorId == null) {
			return null;
		}

		if (direction == Sort.Direction.DESC) {
			return book.id.lt(cursorId);
		}

		return book.id.gt(cursorId);
	}
}
