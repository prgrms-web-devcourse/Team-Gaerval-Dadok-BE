package com.dadok.gaerval.domain.bookshelf.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
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

@DisplayName("bookshelfLike repository 쿼리 테스트")
@CustomDataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BookshelfLikeRepositoryTest {

	private final BookshelfLikeRepository bookshelfLikeRepository;

	private final BookshelfRepository bookshelfRepository;

	private final JobRepository jobRepository;

	private final AuthorityRepository authorityRepository;

	private final UserRepository userRepository;

	private Authority authority;

	private User user;

	private Bookshelf bookshelf;

	@BeforeEach
	void setUp() {
		authority = authorityRepository.save(Authority.create(Role.USER));
		var job = jobRepository.save(JobObjectProvider.backendJob());
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
		user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(authority));
		userRepository.saveAndFlush(user);

		user.changeJob(job);
		bookshelf = bookshelfRepository.save(Bookshelf.create(user));
	}

	@Test
	@DisplayName("조회 - 책장과 사용자을 입력받아 entity 조회")
	void findByUserIdAndBookshelfId() {
		bookshelfLikeRepository.findByUserIdAndBookshelfId(user.getId(), bookshelf.getId());
	}

}