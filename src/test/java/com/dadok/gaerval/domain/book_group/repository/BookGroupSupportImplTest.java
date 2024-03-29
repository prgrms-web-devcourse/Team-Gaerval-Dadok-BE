package com.dadok.gaerval.domain.book_group.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book.repository.BookRepository;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupQueryRequest;
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
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.global.util.SortDirection;
import com.dadok.gaerval.global.util.TimeHolder;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.BookGroupObjectProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;
import com.dadok.gaerval.testutil.TestTimeHolder;
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

	private final TimeHolder timeHolder = TestTimeHolder.now();

	private Authority authority;

	@BeforeEach
	void setUp() {
		authority = authorityRepository.save(Authority.create(Role.USER));
	}

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
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User kakaoUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(kakaoUser);

		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroupObjectProvider.createBookGroup(book, kakaoUser.getId());

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember groupMember = GroupMember.create(bookGroup, kakaoUser, timeHolder);
		groupMemberRepository.saveAndFlush(groupMember);

		bookGroupRepository.findBookGroup(kakaoUser.getId(), bookGroup.getId());
	}

	@DisplayName("findBookGroup 쿼리 테스트 - 그룹장이 아닐시 ")
	@Test
	void findBookGroup_query_notOwner() {
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User kakaoUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(kakaoUser);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroupObjectProvider.createBookGroup(book, kakaoUser.getId());

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser, timeHolder);
		groupMemberRepository.save(kakaoMember);

		GroupMember naverMember = GroupMember.create(bookGroup, naverUser, timeHolder);
		groupMemberRepository.save(naverMember);

		BookGroupDetailResponse group = bookGroupRepository.findBookGroup(naverUser.getId(),
			bookGroup.getId());
		System.out.println(group);
	}

	@DisplayName("findBookGroup 쿼리 테스트 - 멤버가 아닐시 ")
	@Test
	void findBookGroup_query_notMember() {
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User kakaoUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(kakaoUser);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroupObjectProvider.createBookGroup(book, kakaoUser.getId());

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser, timeHolder);
		groupMemberRepository.save(kakaoMember);

		bookGroupRepository.findBookGroup(naverUser.getId(),
			bookGroup.getId());
	}

	@DisplayName("findBookGroup 쿼리 테스트 - ANONYMOUS일시 ")
	@Test
	void findBookGroup_query_anonymous() {
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User kakaoUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(kakaoUser);

		User naverUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));

		userRepository.saveAndFlush(naverUser);
		Book book = BookObjectProvider.createBook();
		bookRepository.saveAndFlush(book);
		BookGroup bookGroup = BookGroupObjectProvider.createBookGroup(book, kakaoUser.getId());

		bookGroupRepository.saveAndFlush(bookGroup);
		GroupMember kakaoMember = GroupMember.create(bookGroup, kakaoUser, timeHolder);
		groupMemberRepository.save(kakaoMember);

		bookGroupRepository.findBookGroup(null,
			bookGroup.getId());
	}

	@DisplayName("findAllByUser 쿼리 테스트")
	@Test
	void findAllByUser_query() {
		BookGroupSearchRequest request = new BookGroupSearchRequest(10, null, SortDirection.DESC);
		bookGroupRepository.findAllByUser(request, 1L);
	}

	@DisplayName("findbyQuery 쿼리 테스트")
	@Test
	void findByQuery_query() {
		BookGroupQueryRequest request = new BookGroupQueryRequest(50, null, null, "hi", null);

		bookGroupRepository.findByQuery(request);
	}
}