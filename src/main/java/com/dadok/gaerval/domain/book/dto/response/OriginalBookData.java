package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

import com.dadok.gaerval.global.config.externalapi.BookApiProvider;

public record OriginalBookData(
	String title,
	List<String> authors,
	String contents,
	String isbn,
	String url,
	String imageUrl,
	String publisher,
	BookApiProvider bookApiProvider
) {
}
