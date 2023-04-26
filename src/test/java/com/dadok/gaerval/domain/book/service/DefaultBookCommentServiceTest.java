package com.dadok.gaerval.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.dadok.gaerval.domain.book.dto.request.BookCommentSearchRequest;
import com.dadok.gaerval.domain.book.dto.response.BookCommentResponses;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.entity.BookComment;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.integration_util.IntegrationTest;

@Tag("Integration Test")
@Sql(scripts = {"/sql/book_comment/book_comment_data.sql"}, executionPhase =
	Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class DefaultBookCommentServiceTest extends IntegrationTest {

	@Autowired
	private DefaultBookCommentService bookCommentService;

	private User user;

	private Book book;

	private BookComment bookComment;

	@BeforeEach
	void setUp() {
		this.user = userRepository.findById(1L).get();
		this.book = bookRepository.findById(1L).get();
		this.bookComment = bookCommentRepository.findById(1L).get();
	}

	@DisplayName("모임에 관한 코멘트 리스트를 받아온다. - 성공")
	@Test
	void findAllBookGroupCommentsByGroup_success() {
		//given
		BookCommentSearchRequest request = new BookCommentSearchRequest(5, null, SortDirection.DESC);
		//when
		BookCommentResponses responses = bookCommentService.findBookCommentsBy(book.getId(),
			user.getId(), request);
		//then
		assertEquals(responses.count(), 1);
		assertEquals(responses.bookComments().get(0).getContents(), bookComment.getComment());
		assertEquals(responses.bookComments().get(0).getNickname(), user.getNickname().nickname());
		assertEquals(responses.bookComments().get(0).getWrittenByCurrentUser(), true);
	}

	@DisplayName("사용자가 null일때 모임에 관한 코멘트 리스트를 받아온다. - 성공")
	@Test
	void findAllBookGroupCommentsByGroup_nullUserId_success() {
		//given
		BookCommentSearchRequest request = new BookCommentSearchRequest(5, null, SortDirection.DESC);
		//when
		BookCommentResponses responses = bookCommentService.findBookCommentsBy(book.getId(),
			null, request);
		//then
		assertEquals(responses.count(), 1);
		assertEquals(responses.bookComments().get(0).getContents(), bookComment.getComment());
		assertEquals(responses.bookComments().get(0).getNickname(), user.getNickname().nickname());
		assertEquals(responses.bookComments().get(0).getWrittenByCurrentUser(), false);
	}

}
