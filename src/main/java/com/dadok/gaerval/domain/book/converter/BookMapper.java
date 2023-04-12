package com.dadok.gaerval.domain.book.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@JacocoExcludeGenerated
public interface BookMapper {

	SearchBookResponse entityToSearchBookResponse(Book book);

	BookResponse entityToBookResponse(Book book);

	@Named("create")
	default Book createBookRequestToEntity(BookCreateRequest bookCreateRequest) {
		return Book.create(bookCreateRequest.title(), bookCreateRequest.author(), bookCreateRequest.isbn(),
			bookCreateRequest.contents(), bookCreateRequest.url(), bookCreateRequest.imageUrl(),
			bookCreateRequest.apiProvider(), bookCreateRequest.publisher());
	}

	BookRecentSearchResponse entityToBookRecentSearchResponse(BookRecentSearch bookRecentSearch);
}
