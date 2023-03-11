package com.dadok.gaerval.domain.book_group.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCommentUpdateRequest;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupComment;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedCommentAuthorException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.integration_util.ServiceIntegration;

@Tag("Integration Test")
@Sql(scripts = {"/sql/book_group_comment/update_test_data.sql"}, executionPhase =
	Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookGroupCommentServiceTest extends ServiceIntegration {

	@Autowired
	private BookGroupCommentService bookGroupCommentService;

	private User user;

	private BookGroup bookGroup;

	private GroupComment groupComment;

	@BeforeEach
	void setUp() {
		this.user = userRepository.findById(1L).get();
		this.groupComment = groupCommentRepository.findById(1L).get();
		this.bookGroup = bookGroupRepository.findById(1L).get();
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