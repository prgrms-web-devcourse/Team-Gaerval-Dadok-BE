package com.dadok.gaerval.domain.book.repository;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookCommentObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookCommentSupportImplTest {

	private final BookCommentRepository bookCommentRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;

	@DisplayName("findAllComments - 책에 대한 코멘트 찾는 쿼리 테스트")
	@Test
	@Transactional
	void findAllComments() {
		User user = UserObjectProvider.createKakaoUser();
		user.changeNickname(new Nickname("티나"));
		User savedUser = userRepository.save(user);

		Book book = BookObjectProvider.createBook();
		Book savedBook = bookRepository.save(book);
		bookCommentRepository.save(BookCommentObjectProvider.create1(savedUser, savedBook));

		BookCommentSearchRequest request = new BookCommentSearchRequest(10, null, null);
		System.out.println(bookCommentRepository.findAllComments(savedBook.getId(), savedUser.getId(), request));
	}

	@DisplayName("existsBy - 코멘트 존재 찾는 쿼리 테스트")
	@Test
	void existsBy() {
		bookCommentRepository.existsBy(BookCommentObjectProvider.commentId1);
	}

	@DisplayName("existsByBookIdAndUserId - 도서 id와 유저 id로 코멘트 존재 찾는 쿼리 테스트")
	@Test
	void existsByBookIdAndUserId() {
		bookCommentRepository.existsByBookIdAndUserId(BookCommentObjectProvider.bookId,
			BookCommentObjectProvider.userId);
	}

	@DisplayName("findByBookIdAndUserId - 도서 id와 유저 id로 코멘트 찾는 쿼리 테스트")
	@Test
	void findByBookIdAndUserId() {
		bookCommentRepository.findByBookIdAndUserId(BookCommentObjectProvider.bookId, BookCommentObjectProvider.userId);
	}

	@DisplayName("updateBookComment - 도서 id와 유저 id로 코멘트 바꾸는 쿼리 테스트")
	@Test
	@Transactional
	void updateBookComment() {

		User user = UserObjectProvider.createKakaoUser();
		user.changeNickname(new Nickname("티나"));
		User savedUser = userRepository.save(user);

		Book book = BookObjectProvider.createBook();
		Book savedBook = bookRepository.save(book);
		bookCommentRepository.save(BookCommentObjectProvider.create1(savedUser, savedBook));

		bookCommentRepository.updateBookComment(savedBook.getId(), savedUser.getId(),
			BookCommentObjectProvider.createCommentUpdateRequest());
	}
}