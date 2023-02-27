package com.dadok.gaerval.domain.book.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.mysema.commons.lang.Assert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Size(min = 1, max = 500)
	private String title;

	@Column(nullable = false)
	@Size(min = 1)
	private String author;

	@Column(nullable = false, unique = true)
	@Size(min = 10, max = 20)
	private String isbn;

	@Column(nullable = false, columnDefinition = "VARCHAR(2000)")
	private String contents;

	@Column(nullable = false)
	private boolean isDeleted;

	@Column(length = 2083)
	private String url;

	@Column(length = 2083, nullable = false)
	private String imageUrl;

	@Column(length = 500)
	private String imageKey;

	@Column(length = 20, nullable = false)
	private String apiProvider;

	protected Book(String title, String author, String isbn, String contents, String url,
		String imageUrl, String apiProvider) {

		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.contents = contents;
		this.url = url;
		this.imageUrl = imageUrl;
		this.apiProvider = apiProvider;
		this.isDeleted = false;

		validateTitle(title);
		validateAuthor(author);
		validateIsbn(isbn);
		validateContents(contents);
		validateUrl(url);
		validateImageUrl(imageUrl);
		validateApiProvider(apiProvider);
	}

	protected Book(String title, String author, String isbn, String contents, String url,
		String imageUrl, String imageKey, String apiProvider) {
		this(title, author, isbn, contents, url, imageUrl, apiProvider);
		this.imageKey = imageKey;

		validateImageKey(imageKey);

	}

	public static Book create(String title, String author, String isbn, String contents, String url,
		String imageUrl, String apiProvider) {
		return new Book(title, author, isbn, contents, url,
			imageUrl, apiProvider);
	}


	public static Book create(String title, String author, String isbn, String contents, String url,
		String imageUrl, String imageKey, String apiProvider) {
		return new Book(title, author, isbn, contents, url,
			imageUrl, imageKey, apiProvider);
	}

	private void validateTitle(String title) {
		Assert.isTrue(title != null && title.length() >= 1 && title.length() <= 500, "책 제목의 길이는 1이상 500 이하 입니다.");
	}

	private void validateAuthor(String author) {
		Assert.isTrue(author != null && author.length() >= 1 && author.length() <= 500, "책 저자의 길이는 1이상 255 이하 입니다.");
	}

	private void validateIsbn(String isbn) {
		Assert.isTrue(isbn != null && isbn.length() >= 10 && isbn.length() <= 20, "ISBN의 길이는 10 이상 20 이하입니다.");
	}

	private void validateContents(String contents) {
		Assert.isTrue(contents != null && contents.length() >= 1, "책 소개의 길이는 1 이상입니다.");
	}

	private void validateUrl(String url) {
		Assert.isTrue(url != null && url.length() <= 2083, "URL의 길이는 2083 이하여야 합니다.");
	}

	private void validateImageUrl(String imageUrl) {
		Assert.isTrue(imageUrl != null && imageUrl.length() <= 2083, "이미지 URL의 길이는 2083 이하여야 합니다.");
	}

	private void validateImageKey(String imageKey) {
		Assert.isTrue(imageKey != null && imageKey.length() <= 500, "이미지 키의 길이는 500 이하여야 합니다.");
	}

	private void validateApiProvider(String apiProvider) {
		Assert.isTrue(apiProvider != null && apiProvider.length() <= 20, "API 제공자의 길이는 20 이하여야 합니다.");
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
