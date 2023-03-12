package com.dadok.gaerval.domain.book.repository;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
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
	private final AuthorityRepository authorityRepository;

	@BeforeEach
	void setUp() {
		authorityRepository.save(Authority.create(Role.USER));
	}

	@DisplayName("findAllComments - 책에 대한 코멘트 찾는 쿼리 테스트")
	@Test
	@Transactional
	void findAllComments() {
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		user.changeNickname(new Nickname("티나"));
		User savedUser = userRepository.save(user);

		Book book = BookObjectProvider.createBook();
		Book savedBook = bookRepository.save(book);
		var comment = bookCommentRepository.save(
			BookCommentObjectProvider.create(savedUser, savedBook, BookCommentObjectProvider.comment1));

		BookCommentSearchRequest request = new BookCommentSearchRequest(10, null, null);
		var res = bookCommentRepository.findAllComments(savedBook.getId(), savedUser.getId(), request);
		assertEquals(res.bookComments().size(), 1);
		assertEquals(res.bookComments().get(0).getContents(), comment.getComment());

		res = bookCommentRepository.findAllComments(55L, savedUser.getId(), request);
		assertEquals(res.bookComments().size(), 0);
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

	@DisplayName("findByBookId - 도서 id로 코멘트 찾는 쿼리 테스트")
	@Test
	void findByBookIdAndUserId() {
		bookCommentRepository.findByBookId(BookCommentObjectProvider.bookId, BookCommentObjectProvider.commentId1);
	}

	@DisplayName("updateBookComment - 도서 id와 유저 id로 코멘트 바꾸는 쿼리 테스트")
	@Test
	@Transactional
	void updateBookComment() {
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		user.changeNickname(new Nickname("티나"));
		User savedUser = userRepository.save(user);

		Book book = BookObjectProvider.createBook();
		Book savedBook = bookRepository.save(book);
		bookCommentRepository.save(
			BookCommentObjectProvider.create(savedUser, savedBook, BookCommentObjectProvider.comment1));

		bookCommentRepository.updateBookComment(savedBook.getId(), savedUser.getId(),
			BookCommentObjectProvider.createCommentUpdateRequest());
	}

}