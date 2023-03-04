package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Slice;

import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

import lombok.Getter;

@Getter
public class BookInShelfResponses {

	private boolean isFirst; // 첫번째냐

	private boolean isLast;  // 마지막이냐

	private boolean hasNext;

	private int count; // 결과 총 갯수

	private boolean isEmpty; // 반환 값이 0개인가

	private List<BookInShelfResponse> books;

	public BookInShelfResponses(Slice<BookshelfItem> bookshelfItemSlice,
		List<BookInShelfResponses.BookInShelfResponse> bookInShelfResponses) {
		this.isFirst = bookshelfItemSlice.isFirst();
		this.isLast = bookshelfItemSlice.isLast();
		this.hasNext = bookshelfItemSlice.hasNext();
		this.count = bookshelfItemSlice.getContent().size();
		this.isEmpty = bookshelfItemSlice.isEmpty();
		this.books = bookInShelfResponses;
	}

	public static BookInShelfResponses empty(Slice<BookshelfItem> bookshelfItemSlice) {

		return new BookInShelfResponses(
			bookshelfItemSlice.isFirst(),
			bookshelfItemSlice.isLast(),
			bookshelfItemSlice.hasNext(),
			0,
			bookshelfItemSlice.isEmpty(),
			Collections.emptyList()
		);
	}

	public BookInShelfResponses(boolean isFirst, boolean isLast, boolean hasNext, int count,
		boolean isEmpty,
		List<BookInShelfResponse> books) {
		this.isFirst = isFirst;
		this.isLast = isLast;
		this.hasNext = hasNext;
		this.count = count;
		this.isEmpty = isEmpty;
		this.books = books;
	}

	@Getter
	public static class BookInShelfResponse {

		private Long bookId;

		private String title;

		private String author;

		private String isbn;

		private String contents;

		private String imageUrl;

		private String url;

		private String publisher;

		public BookInShelfResponse(
			Long bookId,
			String title,
			String author,
			String isbn,
			String contents,
			String imageUrl,
			String url,
			String publisher) {
			this.bookId = bookId;
			this.title = title;
			this.author = author;
			this.isbn = isbn;
			this.contents = contents;
			this.imageUrl = imageUrl;
			this.url = url;
			this.publisher = publisher;
		}
	}

}
