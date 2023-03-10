package com.dadok.gaerval.domain.book.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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

	@Column(nullable = false)
	@Size(min = 0, max = 2000)
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

	@Column(length = 50, nullable = false)
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
		validateLengthInRange(publisher, 1, 50, "출판사");
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

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Book book = (Book)o;
		return isDeleted == book.isDeleted && Objects.equals(id, book.id) && Objects.equals(title,
			book.title) && Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn)
			&& Objects.equals(contents, book.contents) && Objects.equals(url, book.url)
			&& Objects.equals(imageUrl, book.imageUrl) && Objects.equals(imageKey, book.imageKey)
			&& Objects.equals(apiProvider, book.apiProvider) && Objects.equals(publisher,
			book.publisher);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(id, title, author, isbn, contents, isDeleted, url, imageUrl, imageKey, apiProvider,
			publisher);
	}

	public void changeTitle(String title) {
		this.title = title;
		validateLengthInRange(title, 1, 500, "책 제목");
	}

	public void changeAuthor(String author) {
		this.author = author;
		validateLengthInRange(author, 1, 255, "책 저자");
	}

	public void changeContents(String contents) {
		this.contents = contents;
		validateLengthInRange(contents, 1, 2000, "책 소개");
	}

	public void changeUrl(String url) {
		this.url = url;
		validateLengthInRange(url, 0, 2083, "URL");
	}

	public void changeImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		validateLengthInRange(imageUrl, 0, 2083, "이미지 URL");
	}

	public void changeImageKey(String imageKey) {
		this.imageKey = imageKey;
		validateLengthInRange(imageKey, 0, 500, "이미지 키");
	}

	public void changeApiProvider(String apiProvider) {
		this.apiProvider = apiProvider;
		validateLengthInRange(apiProvider, 0, 20, "API 제공자");
	}

	public void changePublisher(String publisher) {
		this.publisher = publisher;
		validateLengthInRange(publisher, 0, 50, "출판사");
	}

	public void change(BookCreateRequest bookCreateRequest) {
		this.changeTitle(bookCreateRequest.title());
		this.changeAuthor(bookCreateRequest.author());
		this.changeContents(bookCreateRequest.contents());
		this.changeApiProvider(bookCreateRequest.apiProvider());
		this.changeUrl(bookCreateRequest.url());
		this.changeImageUrl(bookCreateRequest.imageUrl());
		this.changePublisher(bookCreateRequest.publisher());
	}
}
