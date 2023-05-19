package com.dadok.gaerval.domain.book.repository;

import static com.dadok.gaerval.domain.book.entity.QBookRecentSearch.*;
import static com.dadok.gaerval.domain.user.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponse;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponses;
import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
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
					bookRecentSearch.modifiedAt.coalesce(bookRecentSearch.createdAt).as("modifiedAt")
				)
			)
			.from(bookRecentSearch)
			.innerJoin(user).on(user.id.eq(bookRecentSearch.user.id))
			.where(bookRecentSearch.user.id.eq(userId))
			.orderBy(bookRecentSearch.modifiedAt.coalesce(bookRecentSearch.createdAt).desc())
			.limit(limit)
			.fetch();

		return new BookRecentSearchResponses(bookRecentSearchResponseList);
	}

	@Override
	public void updateRecentSearchKeyword(BookRecentSearch bookRecentSearchData) {
		query.update(bookRecentSearch)
			.where(bookRecentSearch.user.id.eq(bookRecentSearchData.getUser().getId())
				.and(bookRecentSearch.keyword.eq(bookRecentSearchData.getKeyword())))
			.set(bookRecentSearch.modifiedAt, LocalDateTime.now())
			.execute();
	}

	@Override
	public boolean existsByKeywordAndUserId(String keyword, Long userId) {
		Integer fetchOne = query
			.selectOne()
			.from(bookRecentSearch)
			.where(bookRecentSearch.user.id.eq(userId),
				bookRecentSearch.keyword.eq(keyword)
			)
			.fetchFirst();

		return fetchOne != null;
	}

}
