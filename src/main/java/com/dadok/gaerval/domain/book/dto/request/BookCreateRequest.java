package com.dadok.gaerval.domain.book.dto.request;

public record BookCreateRequest(String title, String author, String isbn, String contents, String url,
								String imageUrl, String apiProvider) {
}
