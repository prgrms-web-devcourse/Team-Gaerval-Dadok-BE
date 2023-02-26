package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfItemRepositoryTest {

	private final BookshelfItemRepository bookshelfItemRepository;

	private final User user = User.builder().userAuthority(UserAuthority.of(Role.USER)).build();
	private final Bookshelf bookshelf = Bookshelf.create("영지의 책장", user);
	private final Book book = BookObjectProvider.createRequiredFieldBook();

	@DisplayName("조회 - 책장과 책을 입력받아 bookshelfItem 조회")
	@Test
	void findByBookshelfAndBook() {
		ReflectionTestUtils.setField(bookshelf, "id", 32423L);
		bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book);
	}

}