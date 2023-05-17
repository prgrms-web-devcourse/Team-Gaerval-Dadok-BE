package com.dadok.gaerval.domain.book.converter;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.response.BookRecentSearchResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.OriginalBookData;
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

	default Book originalBookDataToEntity(OriginalBookData originalBookData) {
		String title = StringUtils.isBlank(originalBookData.title()) ? "책 제목 미상" : originalBookData.title();
		String authorsString = originalBookData.authors().isEmpty() ? "저자 미상" : String.join(",", originalBookData.authors());
		String contents = StringUtils.isBlank(originalBookData.contents()) ? "책 소개 미상"
			: (originalBookData.contents().length() >= 1999 ? originalBookData.contents().substring(0, 1998) : originalBookData.contents());
		String isbn = originalBookData.isbn().contains(" ") ? originalBookData.isbn().split(" ")[1] : originalBookData.isbn();
		String publisher = StringUtils.isBlank(originalBookData.publisher()) ? "출판사 미상" : originalBookData.publisher();

		return Book.create(title, authorsString, isbn,
			contents, originalBookData.url(), originalBookData.imageUrl(), originalBookData.bookApiProvider().getName(),
			publisher);
	}
}
