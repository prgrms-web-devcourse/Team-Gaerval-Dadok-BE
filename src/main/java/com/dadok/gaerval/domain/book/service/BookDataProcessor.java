package com.dadok.gaerval.domain.book.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.global.config.security.AuthProvider;

public class BookDataProcessor {

	public static Book process(String title, List<String> authors, String contents, String isbn, String url,
		String imageUrl,
		String publisher) {

		if (StringUtils.isBlank(title)) {
			title = "책 제목 미상";
		}

		String authorsString = String.join(",", authors);
		if (authors.isEmpty()) {
			authorsString = "저자 미상";
		}

		if (StringUtils.isBlank(contents)) {
			contents = "책 소개 미상";
		} else {
			if (contents.length() >= 1999)
				contents = contents.substring(0, 1998);
		}

		isbn = isbn.contains(" ") ? isbn.split(" ")[1] : isbn;

		if (StringUtils.isBlank(publisher)) {
			publisher = "출판사 미상";
		}

		return Book.create(title, authorsString, isbn,
			contents, url, imageUrl, AuthProvider.KAKAO.getName(),
			publisher);

	}
}
