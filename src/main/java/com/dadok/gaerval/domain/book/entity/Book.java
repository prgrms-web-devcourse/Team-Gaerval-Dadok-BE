package com.dadok.gaerval.domain.book.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseTimeColumn {

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

	@Column(nullable = false)
	@Size(min = 1, max = 2000)
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

	@Column(length = 20, nullable = false)
	private String publisher;

	protected Book(String title, String author, String isbn, String contents, String url,
		String imageUrl, String apiProvider, String publisher) {

		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.contents = contents;
		this.url = url;
		this.imageUrl = imageUrl;
		this.apiProvider = apiProvider;
		this.isDeleted = false;
		this.publisher = publisher;

		validateLengthInRange(title, 1, 500, "책 제목");
		validateLengthInRange(author, 1, 255, "책 저자");
		validateLengthInRange(isbn, 10, 20, "ISBN");
		validateLengthInRange(contents, 1, 2000, "책 소개");
		validateLengthInRange(url, 0, 2083, "URL");
		validateLengthInRange(imageUrl, 0, 2083, "이미지 URL");
		validateLengthInRange(apiProvider, 0, 20, "API 제공자");
		validateLengthInRange(publisher, 1, 20, "출판사");
	}

	protected Book(String title, String author, String isbn, String contents, String url,
		String imageUrl, String imageKey, String apiProvider, String publisher) {
		this(title, author, isbn, contents, url, imageUrl, apiProvider, publisher);
		this.imageKey = imageKey;
		validateLengthInRange(imageKey, 0, 500, "이미지 키");
	}

	public static Book create(String title, String author, String isbn, String contents, String url,
		String imageUrl, String apiProvider, String publisher) {
		return new Book(title, author, isbn, contents, url,
			imageUrl, apiProvider, publisher);
	}

	public static Book create(String title, String author, String isbn, String contents, String url,
		String imageUrl, String imageKey, String apiProvider, String publisher) {
		return new Book(title, author, isbn, contents, url,
			imageUrl, imageKey, apiProvider, publisher);
	}

	public void changeDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
