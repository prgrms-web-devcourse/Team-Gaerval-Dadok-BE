package com.dadok.gaerval.domain.book.repository;

import static com.dadok.gaerval.domain.book.entity.QBook.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelf.*;
import static com.dadok.gaerval.domain.bookshelf.entity.QBookshelfItem.*;
import static com.dadok.gaerval.domain.job.entity.QJob.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponse;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.global.util.QueryDslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookSupportImpl implements BookSupport {

	private final JPAQueryFactory query;

	// 많이 꽂은 책순
	@Override
	public SuggestionsBookFindResponses findSuggestionBooks(SuggestionsBookFindRequest request) {

		Sort.Direction direction = request.sortDirection().toDirection();

		List<SuggestionsBookFindResponse> bookFindResponses = query.select(
				Projections.constructor(SuggestionsBookFindResponse.class,
					bookshelfItem.book.id.as("bookId"),
					book.imageUrl.as("imageUrl"),
					book.title.as("title"),
					book.author.as("author"),
					book.isbn.as("isbn"),
					book.publisher.as("publisher"),
					book.url.as("url"),
					job.jobGroup.as("jobGroup"),
					bookshelfItem.book.id.count().as("count")
				))
			.from(bookshelfItem)
			.innerJoin(bookshelfItem.bookshelf, bookshelf)
			.innerJoin(bookshelfItem.book, book)
			.innerJoin(job).on(job.id.eq(bookshelf.jobId))
			.where(generateCursorId(request.bookCursorId(), direction),
				job.jobGroup.eq(request.jobGroup())
			)
			.limit(request.pageSize() + 1)
			.groupBy(bookshelfItem.book.id, job.jobGroup)
			.orderBy(bookshelfItem.book.id.count().desc())
			.fetch();

		Slice<SuggestionsBookFindResponse> books = QueryDslUtil.toSlice(bookFindResponses,
			PageRequest.of(0, request.pageSize(),
				Sort.by(direction, "bookshelfItem.book_id")));


		return new SuggestionsBookFindResponses(books, request.jobGroup());
	}

	private BooleanExpression generateCursorId(Long cursorId, Sort.Direction direction) {
		if (cursorId == null) {
			return null;
		}

		if (direction == Sort.Direction.DESC) {
			return bookshelfItem.book.id.lt(cursorId);
		}

		return bookshelfItem.book.id.gt(cursorId);
	}

}
