package com.dadok.gaerval.testutil;

import java.time.LocalDate;

import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public class BookGroupObjectProvider {

	public static BookGroup createMockBookGroup(Book book, Long ownerId) {
		BookGroup bookGroup = BookGroup.create(
			ownerId, book, LocalDate.now(), LocalDate.now(),
			5, "책읽기 소모임", "책읽기 소모임", true
		);
		ReflectionTestUtils.setField(bookGroup, "id", 234L);
		return bookGroup;
	}

}
