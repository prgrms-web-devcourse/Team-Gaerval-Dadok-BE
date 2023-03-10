package com.dadok.gaerval.domain.bookshelf.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.fasterxml.jackson.annotation.JsonBackReference;

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Bookshelf_item",
	uniqueConstraints = {
		@UniqueConstraint(name = "bookshelf_id_book_id_unique_key",
			columnNames = {"bookshelf_id", "book_id"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookshelfItem extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "bookshelf_id")
	private Bookshelf bookshelf;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "item_type", nullable = false, columnDefinition = "varchar(20)")
	@Enumerated(EnumType.STRING)
	private BookshelfItemType type;

	private BookshelfItem(Bookshelf bookshelf, Book book, BookshelfItemType type) {
		Assert.notNull(bookshelf, "BookshelfItem의 bookshelf는 null일 수 없습니다.");
		Assert.notNull(book, "BookshelfItem의 book은 null일 수 없습니다.");
		Assert.notNull(type, "BookshelfItem의 type은 null일 수 없습니다.");

		this.bookshelf = bookshelf;
		bookshelf.addBookShelfItem(this);
		this.book = book;
		this.type = type;
	}

	public static BookshelfItem create(Bookshelf bookshelf, Book book, BookshelfItemType type) {
		return new BookshelfItem(bookshelf, book, type);
	}

	public static BookshelfItem create(Bookshelf bookshelf, Book book) {
		return new BookshelfItem(bookshelf, book, BookshelfItemType.READ);
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BookshelfItem that = (BookshelfItem)o;
		return Objects.equals(bookshelf, that.bookshelf) && Objects.equals(book, that.book);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(bookshelf, book);
	}
}
