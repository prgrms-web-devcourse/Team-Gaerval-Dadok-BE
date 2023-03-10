package com.dadok.gaerval.domain.bookshelf.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

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
	public boolean existsByBookshelfIdAndBookId(Long bookshelfId, Long bookId) {
		Integer fetchOne = query.selectOne().from(bookshelfItem)
			.where(bookshelfItem.bookshelf.id.eq(bookshelfId), bookshelfItem.book.id.eq(bookId))
			.fetchFirst();

		return fetchOne != null;
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

}
