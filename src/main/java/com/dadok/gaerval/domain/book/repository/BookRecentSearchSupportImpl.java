package com.dadok.gaerval.domain.book.repository;

import static com.dadok.gaerval.domain.book.entity.QBookRecentSearch.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;

import java.util.List;

import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponse;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookRecentSearchSupportImpl implements BookRecentSearchSupport {

	private final JPAQueryFactory query;

	@Override
	public BookRecentSearchResponses findRecentSearches(Long userId, Long limit) {
		List<BookRecentSearchResponse> bookRecentSearchResponseList = query.select(
				Projections.constructor(BookRecentSearchResponse.class,
					bookRecentSearch.keyword.as("keyword"),
					bookRecentSearch.createdAt.as("createdAt")
				)
			)
			.from(bookRecentSearch)
			.innerJoin(user).on(user.id.eq(bookRecentSearch.user.id))
			.where(bookRecentSearch.user.id.eq(userId))
			.orderBy(bookRecentSearch.createdAt.desc())
			.limit(limit)
			.fetch();

		return new BookRecentSearchResponses(bookRecentSearchResponseList);
	}
}
