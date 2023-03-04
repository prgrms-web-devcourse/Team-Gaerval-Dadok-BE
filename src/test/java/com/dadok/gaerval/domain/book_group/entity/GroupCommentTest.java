package com.dadok.gaerval.domain.book_group.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dadok.gaerval.domain.book_group.exception.InvalidCommentException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

class GroupCommentTest {

	@DisplayName("create - contents 글자가 2000자가 넘어가면 생성에 실패한다.")
	@Test
	void create_fail_lengthOver() {
		//given
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, 2002).forEach(sb::append);

		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();

		String contents = sb.toString();
		//when
		assertThrows(InvalidArgumentException.class,
			() -> GroupComment.create(contents, bookGroup, kakaoUser));
	}

	@DisplayName("create - 생성에 성공한다.")
	@Test
	void create_success() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();

		String contents = "우리 모임 너무 좋아요";
		//when
		GroupComment groupComment = assertDoesNotThrow(() -> GroupComment.create(contents, bookGroup, kakaoUser));

		//then
		assertEquals(bookGroup, groupComment.getBookGroup());
		assertTrue(bookGroup.getComments().contains(groupComment));
	}

	@DisplayName("create - Child Comment 생성에 성공한다.")
	@Test
	void createChild_success() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		String contents = "어머 저도 좋아요";

		//when
		GroupComment childComment = GroupComment.createChild(contents, bookGroup, naverUser, parentComment);

		//then
		assertTrue(parentComment.getChildComments().contains(childComment));
		assertEquals(naverUser, childComment.getUser());
		assertEquals(contents, childComment.getContents());
		assertEquals(childComment.getParentComment(), parentComment);
		assertEquals(bookGroup, childComment.getBookGroup());
		assertTrue(bookGroup.getComments().contains(childComment));
	}

	@DisplayName("addParent - parent가 부모가 아니면 Parent를 바꾸는데 실패한다.")
	@Test
	void addParent_fail() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		GroupComment childComment = GroupComment.createChild("자식이에요", bookGroup, naverUser, parentComment);

		//when
		GroupComment groupComment = GroupComment.create("나도 자식이에요", bookGroup, kakaoUser);
		assertThrows(InvalidCommentException.class, () -> groupComment.addParent(childComment));
	}

	@DisplayName("adChild - GroupComment 자신이 Parent가 아니면 addChild에 실패한다.")
	@Test
	void addChild_fail_selfNotParent() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		GroupComment childComment = GroupComment.createChild("자식이에요", bookGroup, naverUser, parentComment);

		//when
		GroupComment otherChild = GroupComment.createChild("나도 자식이에요", bookGroup, kakaoUser, parentComment);

		assertThrows(InvalidCommentException.class, () -> childComment.addChild(otherChild));
	}

	@DisplayName("adChild - child가 Parent가 아니면 addChild에 실패한다.")
	@Test
	void addChild_fail_childNotParent() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		GroupComment OtherParent = GroupComment.create("자식이에요", bookGroup, naverUser);

		//when
		GroupComment child = GroupComment.createChild("나도 자식이에요", bookGroup, kakaoUser, parentComment);

		assertThrows(InvalidCommentException.class, () -> child.addChild(OtherParent));
	}

	@DisplayName("isParent : 부모가 없으면 true")
	@Test
	void isParent_true() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);

		//when & then
		assertTrue(parentComment.isParent());
	}

	@DisplayName("isParent : 부모가 있으면 false")
	@Test
	void isParent_false() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		GroupComment childComment = GroupComment.createChild("자식이에요", bookGroup, naverUser, parentComment);

		//when & then
		assertFalse(childComment.isParent());
	}

	@DisplayName("isChild : 부모가 있으면 true")
	@Test
	void isChild_true() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		User naverUser = UserObjectProvider.createNaverUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);
		GroupComment childComment = GroupComment.createChild("자식이에요", bookGroup, naverUser, parentComment);

		//when & then
		assertTrue(childComment.isChild());
	}

	@DisplayName("isChild : 부모가 없으면 false")
	@Test
	void isChild_false() {
		//given
		BookGroup bookGroup = BookGroupObjectProvider.createMockBookGroup(BookObjectProvider.createAllFieldBook(),
			1L);
		User kakaoUser = UserObjectProvider.createKakaoUser();
		GroupComment parentComment = GroupComment.create("우리 모임 너무 좋아요", bookGroup, kakaoUser);

		//when & then
		assertFalse(parentComment.isChild());
	}
}