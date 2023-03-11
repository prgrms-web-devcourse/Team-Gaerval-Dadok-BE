package com.dadok.gaerval.domain.book_group.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedCommentAuthorException;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.integration_util.ServiceIntegration;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
import com.dadok.gaerval.testutil.UserObjectProvider;

@Tag("Integration Test")
@Sql(scripts = {"/sql/clean_up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookGroupCommentServiceTest extends ServiceIntegration {

	@Autowired
	private BookGroupCommentService bookGroupCommentService;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	private User user;

	private Book book;

	private BookGroup bookGroup;

	private GroupComment groupComment;

	@BeforeEach
	void setUp() {
		TransactionStatus transaction = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			Authority authority = authorityRepository.save(Authority.create(Role.USER));
			User user = UserObjectProvider.createKakaoUser();
			user.getAuthorities().add(UserAuthority.create(authority));
			this.user = userRepository.save(user);
			Book book = BookObjectProvider.createBook();
			this.book = bookRepository.save(book);
			BookGroup bookGroup = BookGroup.create(user.getId(),
				book, LocalDate.now(), LocalDate.now().plusDays(7), 5,
				"책모임", "책모임이에요", false, null, null, true,
				passwordEncoder, TestTimeHolder.now());
			this.bookGroup = bookGroupRepository.save(bookGroup);
			GroupComment comment = GroupComment.create("내용", bookGroup, user);
			this.groupComment = groupCommentRepository.save(comment);
			platformTransactionManager.commit(transaction);
		} catch (Exception e) {
			platformTransactionManager.rollback(transaction);
		}
	}

	@DisplayName("코멘트의 내용이 바뀐다.")
	@Test
	void updateBookGroupComment_success() {
		//given
		String changedContents = "바뀐 내용";
		BookGroupCommentUpdateRequest commentUpdateRequest = new BookGroupCommentUpdateRequest(changedContents);
		//when
		bookGroupCommentService.updateBookGroupComment(bookGroup.getId(), user.getId(), groupComment.getId(),
			commentUpdateRequest);
		//then
		GroupComment findGroupComment = groupCommentRepository.findById(groupComment.getId()).get();
		assertEquals(findGroupComment.getContents(), changedContents);
	}

	@DisplayName("작성자가 아닌 사람이 요청하면, 예외를 던진다")
	@Test
	void updateBookGroupComment_throw() {
		//given
		String changedContents = "바뀐 내용";
		BookGroupCommentUpdateRequest commentUpdateRequest = new BookGroupCommentUpdateRequest(changedContents);
		Long otherUserId = 1000L;

		//when
		assertThrows(NotMatchedCommentAuthorException.class,
			() -> bookGroupCommentService.updateBookGroupComment(bookGroup.getId(), otherUserId, groupComment.getId(),
				commentUpdateRequest));
		//then
		GroupComment findGroupComment = groupCommentRepository.findById(groupComment.getId()).get();
		assertNotEquals(findGroupComment.getContents(), changedContents);
	}

}