package com.dadok.gaerval.domain.book.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookObjectProvider;


@CustomDataJpaTest
class BookRepositoryTest {
	@Autowired
	private BookRepository bookRepository;

	@Test
	@DisplayName("존재하는 ISBN으로 하나의 도서를 찾아오는데 성공한다.")
	void findIsbn_success() {
		// given
		Book book = BookObjectProvider.createAllFieldBook();
		bookRepository.save(book);

		// when
		Optional<Book> findBook = bookRepository.findBookByIsbn(BookObjectProvider.isbn);
		System.out.println(findBook);

		// then
		assertTrue(findBook.isPresent());
		assertEquals(book.getIsbn(), findBook.get().getIsbn());
	}
}
