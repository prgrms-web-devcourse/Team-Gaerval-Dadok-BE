package com.dadok.gaerval.domain.bookshelf.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.bookshelf.dto.request.LikedBookShelvesRequest;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfLike;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.repository.JobRepository;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.repository.CustomDataJpaTest;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

import lombok.RequiredArgsConstructor;

@DisplayName("bookshelf repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfRepositoryTest {

	private final BookshelfRepository bookshelfRepository;

	private final BookshelfLikeRepository bookshelfLikeRepository;

	private final JobRepository jobRepository;

	private final AuthorityRepository authorityRepository;

	private final UserRepository userRepository;

	private Authority authority;

	@BeforeEach
	void setUp() {
		authority = authorityRepository.save(Authority.create(Role.USER));
	}

	@DisplayName("findBookShelfByOwnerId 쿼리 테스트")
	@Test
	void findByIdWithUserAndJob() {
		bookshelfRepository.findBookShelfByOwnerId(100L, null);
	}

	@DisplayName("사용자의 책장 요약 조회")
	@Test
	void findSummaryByUser() {
		// Given
		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(user);

		user.changeJob(job);
		var bookshelf = bookshelfRepository.save(Bookshelf.create(user));
		// When
		var res = bookshelfRepository.findSummaryById(bookshelf.getUser().getId());
		// Then
		assertThat(res.isPresent()).isTrue();
	}

	@DisplayName("사용자의 책장 요약 조회_empty 반환")
	@Test
	void findSummaryByUser_empty() {
		// When
		var res = bookshelfRepository.findSummaryById(3L);
		// Then
		assertThat(res.isEmpty()).isTrue();
	}

	@DisplayName("직군별 인기 책장 요약 list 조회 - findSuggestionsByJobGroup 쿼리테스트")
	@Test
	void findSuggestionsByJobGroup() {
		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(UserObjectProvider.kakaoAttribute(), UserAuthority.create(authority));
		User otherUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));
		userRepository.saveAllAndFlush(List.of(user, otherUser));

		var bookshelf = bookshelfRepository.saveAndFlush(Bookshelf.create(user));
		var bookshelf2 = bookshelfRepository.saveAndFlush(Bookshelf.create(otherUser));

		bookshelf.changeJobId(job.getId());
		bookshelf2.changeJobId(job.getId());

		var res = bookshelfRepository.findSuggestionsByJobGroup(JobGroup.DEVELOPMENT, user.getId(), 2);
		assertThat(res.size()).isEqualTo(1);
	}

	@DisplayName("직군별 인기 책장 요약 emtpy list 조회 - findSuggestionsByJobGroup 쿼리테스트")
	@Test
	void findSuggestionsByJobGroup_empty() {
		bookshelfRepository.findSuggestionsByJobGroup(JobGroup.DEVELOPMENT, 10L, 1);
	}

	@DisplayName("인기 책장 요약 list 조회 - findAllSuggestions 쿼리테스트")
	@Test
	void findAllSuggestions() {
		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(UserObjectProvider.kakaoAttribute(), UserAuthority.create(authority));
		User otherUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));
		userRepository.saveAllAndFlush(List.of(user, otherUser));

		user.changeJob(job);
		otherUser.changeJob(job);
		bookshelfRepository.saveAndFlush(Bookshelf.create(user));
		bookshelfRepository.saveAndFlush(Bookshelf.create(otherUser));

		var res = bookshelfRepository.findAllSuggestions(5);
		assertThat(res.size()).isEqualTo(2);
	}

	@DisplayName("인기 책장 요약 empty list 조회 - findAllSuggestions 쿼리테스트")
	@Test
	void findAllSuggestions_empty() {
		bookshelfRepository.findAllSuggestions(1);
	}

	@DisplayName("findBookShelfById 쿼리 테스트")
	@Test
	void findBookShelfById() {
		bookshelfRepository.findBookShelfById(100L, null);
	}

	@DisplayName("좋아요한 책장 요약 list 조회 - findAllLikedByUserId 쿼리 테스트")
	@Test
	void findAllLikedByUserId() {

		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(UserObjectProvider.kakaoAttribute(), UserAuthority.create(authority));
		User requestUser = User.createByOAuth(UserObjectProvider.naverAttribute(), UserAuthority.create(authority));
		userRepository.saveAllAndFlush(List.of(user, requestUser));

		user.changeJob(job);
		requestUser.changeJob(job);
		Bookshelf bookshelf = bookshelfRepository.saveAndFlush(Bookshelf.create(user));
		bookshelfLikeRepository.saveAndFlush(BookshelfLike.create(requestUser, bookshelf));

		LikedBookShelvesRequest request = new LikedBookShelvesRequest(10, null, null);

		var response = bookshelfRepository.findAllLikedByUserId(request, requestUser.getId());
		assertThat(response.count()).isEqualTo(1);
		assertThat(response.bookshelfResponses().get(0).likeCount()).isEqualTo(1);
	}

	@DisplayName("좋아요한 책장 empty list 조회 - findAllLikedByUserId 쿼리 테스트")
	@Test
	void findAllLikedByUserId_empty() {

		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		User user = User.createByOAuth(UserObjectProvider.kakaoAttribute(), UserAuthority.create(authority));
		userRepository.saveAndFlush(user);
		user.changeJob(job);

		LikedBookShelvesRequest request = new LikedBookShelvesRequest(10, null, null);

		var response = bookshelfRepository.findAllLikedByUserId(request, user.getId());
		assertThat(response.bookshelfResponses().size()).isEqualTo(0);
	}
}