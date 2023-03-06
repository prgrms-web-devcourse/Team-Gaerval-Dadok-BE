package com.dadok.gaerval.domain.book_group.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;
import com.dadok.gaerval.domain.book_group.entity.GroupMember;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookGroupSupportImplTest {

	private final BookGroupRepository bookGroupRepository;

	private final UserRepository userRepository;

	private final BookRepository bookRepository;

	private final GroupMemberRepository groupMemberRepository;

	private final AuthorityRepository authorityRepository;

	@DisplayName("findAllBy 쿼리 테스트")
	@Test
	void findAllBy_query() {
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);
		bookGroupRepository.findAllBy(request);
	}

	@DisplayName("findBookGroup 쿼리 테스트")
	@Test
	void findBookGroup_query_throw() {
		assertThrows(ResourceNotfoundException.class,
			() -> bookGroupRepository.findBookGroup(1L, 1L));

	}

	@DisplayName("findBookGroup 쿼리 테스트 - 그룹장일시 ")
	@Test
	void findBookGroup_query_onwer() {

		User kakaoUser = UserObjectProvider.createKakaoUser();
		userRepository.saveAndFlush(kakaoUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroup.create(kakaoUser.getId(),
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			4, "북그룹", "소개합니다", true, "월든 작가는?", "헨리데이빗소로우"
		);

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember groupMember = GroupMember.create(bookGroup, kakaoUser);
		groupMemberRepository.saveAndFlush(groupMember);

		bookGroupRepository.findBookGroup(kakaoUser.getId(), bookGroup.getId());
	}

	@DisplayName("findBookGroup 쿼리 테스트 - 그룹장이 아닐시 ")
	@Test
	void findBookGroup_query_notOwner() {

		User kakaoUser = UserObjectProvider.createKakaoUser();
		userRepository.saveAndFlush(kakaoUser);
		Authority authority = authorityRepository.getReferenceById(Role.USER);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroup.create(kakaoUser.getId(),
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", true, "월든 작가는?", "헨리데이빗소로우"
		);

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser);
		groupMemberRepository.save(kakaoMember);

		GroupMember naverMember = GroupMember.create(bookGroup, naverUser);
		groupMemberRepository.save(naverMember);

		BookGroupDetailResponse group = bookGroupRepository.findBookGroup(naverUser.getId(),
			bookGroup.getId());
		System.out.println(group);
	}

	@DisplayName("findBookGroup 쿼리 테스트 - 멤버가 아닐시 ")
	@Test
	void findBookGroup_query_notMember() {

		User kakaoUser = UserObjectProvider.createKakaoUser();
		userRepository.saveAndFlush(kakaoUser);
		Authority authority = authorityRepository.getReferenceById(Role.USER);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroup.create(kakaoUser.getId(),
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", true, "월든 작가는?", "헨리데이빗소로우"
		);

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser);
		groupMemberRepository.save(kakaoMember);

		bookGroupRepository.findBookGroup(naverUser.getId(),
			bookGroup.getId());
	}

	@DisplayName("findBookGroup 쿼리 테스트 - ANONYMOUS일시 ")
	@Test
	void findBookGroup_query_anonymous() {

		User kakaoUser = UserObjectProvider.createKakaoUser();
		userRepository.saveAndFlush(kakaoUser);
		Authority authority = authorityRepository.getReferenceById(Role.USER);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroup.create(kakaoUser.getId(),
			book, LocalDate.now().plusDays(1), LocalDate.now().plusDays(7),
			6, "북그룹", "소개합니다", true, "월든 작가는?", "헨리데이빗소로우"
		);

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser);
		groupMemberRepository.save(kakaoMember);

		bookGroupRepository.findBookGroup(null,
			bookGroup.getId());
	}

	@DisplayName("findAllByUser 쿼리 테스트")
	@Test
	void findAllByUser_query() {
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);
		bookGroupRepository.findAllByUser(request, 3L);
	}

}