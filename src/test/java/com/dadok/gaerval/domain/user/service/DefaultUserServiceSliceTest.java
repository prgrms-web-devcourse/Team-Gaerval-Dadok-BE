package com.dadok.gaerval.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.bookshelf.service.BookshelfService;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.job.service.JobService;
import com.dadok.gaerval.domain.user.dto.request.UserChangeProfileRequest;
import com.dadok.gaerval.domain.user.dto.request.UserJobChangeRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobChangeResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.domain.user.exception.DuplicateNicknameException;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.error.exception.DuplicateException;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceSliceTest {

	@InjectMocks
	private DefaultUserService defaultUserService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthorityRepository authorityRepository;

	@Mock
	private JobService jobService;

	@Mock
	private BookshelfService bookshelfService;

	private Role roleUser = Role.USER;
	private AuthProvider kakao = AuthProvider.KAKAO;
	private Authority RoleUserAuthority = Authority.create(roleUser);

	@DisplayName("register - authority를 찾아 유저를 저장하고 반환한다 - 성공")
	@Test
	void register_findWithAuthority_success() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User expectedUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));
		given(authorityRepository.findById(roleUser))
			.willReturn(Optional.of(RoleUserAuthority));

		given(userRepository.save(expectedUser))
			.willReturn(expectedUser);

		given(bookshelfService.createBookshelf(expectedUser))
			.willReturn(1L);

		//when
		User register = defaultUserService.register(oAuth2Attribute);
		//then

		assertEquals(register, expectedUser);
		verify(authorityRepository).findById(roleUser);
		verify(userRepository).save(expectedUser);
		verify(bookshelfService).createBookshelf(expectedUser);
	}

	@DisplayName("register - authority를 새로 저장하고 유저를 저장하고 반환한다 - 성공")
	@Test
	void register_notFindCreateAuthority_success() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User expectedUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));

		given(authorityRepository.findById(roleUser))
			.willReturn(Optional.empty());

		given(authorityRepository.save(RoleUserAuthority))
			.willReturn(RoleUserAuthority);

		given(userRepository.save(expectedUser))
			.willReturn(expectedUser);

		//when
		User register = defaultUserService.register(oAuth2Attribute);

		//then
		assertEquals(register, expectedUser);
		verify(authorityRepository).findById(roleUser);
		verify(authorityRepository).save(RoleUserAuthority);
		verify(userRepository).save(expectedUser);
	}

	@DisplayName("findById - Optional<User>를 반환한다.")
	@Test
	void findById_withNonnull_success() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(userId))
			.willReturn(Optional.of(user));

		//when
		Optional<User> userOptional = defaultUserService.findById(userId);

		//then
		assertTrue(userOptional.isPresent());
		User findUser = userOptional.get();
		assertEquals(user, findUser);
		verify(userRepository).findById(userId);
	}

	@DisplayName("findById - Optional.EMPTY를 반환한다.")
	@Test
	void findById_withNull_success() {
		//given
		Long userId = 1L;

		given(userRepository.findById(userId))
			.willReturn(Optional.empty());

		//when
		Optional<User> userOptional = defaultUserService.findById(userId);

		//then
		assertTrue(userOptional.isEmpty());
		verify(userRepository).findById(userId);
	}

	@DisplayName("getById - User를 반환한다.")
	@Test
	void getById_found_success() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(userId))
			.willReturn(Optional.of(user));

		//when
		User findUser = defaultUserService.getById(userId);

		//then
		assertEquals(user, findUser);
		verify(userRepository).findById(userId);
	}

	@DisplayName("getById - User가 없으면 예외를 던진다.")
	@Test
	void getById_notfound_throw() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User user = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(userId))
			.willReturn(Optional.empty());

		//when
		assertThrows(ResourceNotfoundException.class,
			() -> defaultUserService.getById(userId));
		verify(userRepository).findById(userId);
	}

	@DisplayName("findByEmailWithAuthorities - email로 Authority들을 조인해서 반환한다.")
	@Test
	void findByEmailWithAuthorities() {
		//given
		Map<String, Object> attributes = UserObjectProvider.attributes(kakao);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, UserObjectProvider.KAKAO_ATTRIBUTE_KEY,
			attributes);

		User expectedUser = User.createByOAuth(oAuth2Attribute, UserAuthority.create(RoleUserAuthority));
		String email = expectedUser.getEmail();
		given(userRepository.findTopByEmailWithAuthorities(email))
			.willReturn(Optional.of(expectedUser));

		//when
		Optional<User> userOptional = defaultUserService.findByEmailWithAuthorities(email);

		//then
		assertTrue(userOptional.isPresent());
		User findUser = userOptional.get();
		assertEquals(expectedUser, findUser);
		assertEquals(1, findUser.getAuthorities().size());
		verify(userRepository).findTopByEmailWithAuthorities(email);
	}

	@DisplayName("getUserDetail - 유저의 정보를 조회해온다.")
	@Test
	void getUserDetail_success() {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("임시닉네임"));

		UserDetailResponse mockUserDetailResponse = new UserDetailResponse(userId, kakaoUser.getName(),
			kakaoUser.getNickname().nickname(), kakaoUser.getOauthNickname(), kakaoUser.getEmail(),
			kakaoUser.getProfileImage(), kakaoUser.getGender(), kakaoUser.getAuthProvider(), JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER, 1);

		given(userRepository.findUserDetail(userId))
			.willReturn(mockUserDetailResponse);

		//when
		UserDetailResponse userDetailResponse = defaultUserService.getUserDetail(userId);

		//then
		assertEquals(mockUserDetailResponse, userDetailResponse);
		verify(userRepository).findUserDetail(userId);
	}

	@DisplayName("getUserDetail - 유저가 존재하지 않으면 예외를 던진다.")
	@Test
	void getUserDetail_throw() {
		//given
		Long userId = 1L;
		given(userRepository.findUserDetail(userId))
			.willReturn(null);

		//when
		assertThrows(ResourceNotfoundException.class, () -> defaultUserService.getUserDetail(userId));
		verify(userRepository).findUserDetail(userId);
	}

	@DisplayName("getUserProfile - 유저의 정보를 조회해온다.")
	@Test
	void getUserProfile_success() {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("임시닉네임"));

		var mockUserProfileResponse = new UserProfileResponse(userId,
			kakaoUser.getNickname().nickname(), kakaoUser.getProfileImage(), kakaoUser.getGender(),
			JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER, 1);

		given(userRepository.findUserProfile(userId))
			.willReturn(mockUserProfileResponse);

		//when
		var userProfileResponse = defaultUserService.getUserProfile(userId);

		//then
		assertEquals(mockUserProfileResponse, userProfileResponse);
		verify(userRepository).findUserProfile(userId);
	}

	@DisplayName("getUserProfile - 유저가 존재하지 않으면 예외를 던진다.")
	@Test
	void getUserProfile_throw() {
		//given
		Long userId = 1L;
		given(userRepository.findUserProfile(userId))
			.willReturn(null);

		//when
		assertThrows(ResourceNotfoundException.class, () -> defaultUserService.getUserProfile(userId));
		verify(userRepository).findUserProfile(userId);
	}

	@DisplayName("registerJob - 유저의 Job을 바꾼다.")
	@Test
	void registerJob() {
		//given
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.getReferenceById(userId))
			.willReturn(user);

		Job backendJob = JobObjectProvider.backendJob();

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;

		given(jobService.getBy(development, backendDeveloper))
			.willReturn(backendJob);

		UserJobChangeRequest userJobChangeRequest = new UserJobChangeRequest(development, backendDeveloper);
		//when

		UserJobChangeResponse response = defaultUserService.changeJob(userId,
			userJobChangeRequest);

		//then
		assertThat(user.getJob()).isEqualTo(backendJob);

		assertThat(response)
			.hasFieldOrPropertyWithValue("userId", userId);
		assertThat(response.job())
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", development.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", development)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", backendDeveloper.getJobName())
			.hasFieldOrPropertyWithValue("jobName", backendDeveloper)
			.hasFieldOrPropertyWithValue("order", backendJob.getSortOrder());
		verify(userRepository).getReferenceById(userId);
		verify(jobService).getBy(development, backendDeveloper);
	}

	@DisplayName("changeProfile - 프로필 변경에 성공한다.")
	@Test
	void changeProfile_success() {
		//given
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;
		Job backendJob = JobObjectProvider.backendJob();

		String changeNickname = "nickname";
		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(development,
				backendDeveloper));

		given(userRepository.getReferenceById(userId))
			.willReturn(user);

		given(jobService.getBy(development, backendDeveloper))
			.willReturn(backendJob);

		Nickname nickname = new Nickname(changeNickname);
		given(userRepository.existsByNickname(nickname))
			.willReturn(false);

		willDoNothing().given(bookshelfService)
			.updateJobIdByUserId(userId, backendJob.getId());

		//when
		UserDetailResponse response = defaultUserService.changeProfile(userId, request);

		//then
		assertThat(user.getJob()).isEqualTo(backendJob);

		assertThat(response)
			.hasFieldOrPropertyWithValue("userId", userId)
			.hasFieldOrPropertyWithValue("nickname", changeNickname)
			.hasFieldOrPropertyWithValue("oauthNickname", user.getOauthNickname())
			.hasFieldOrPropertyWithValue("email", user.getEmail())
			.hasFieldOrPropertyWithValue("profileImage", user.getProfileImage())
			.hasFieldOrPropertyWithValue("gender", user.getGender())
			.hasFieldOrPropertyWithValue("authProvider", user.getAuthProvider());

		assertThat(response.job())
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", development.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", development)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", backendDeveloper.getJobName())
			.hasFieldOrPropertyWithValue("jobName", backendDeveloper)
			.hasFieldOrPropertyWithValue("order", backendJob.getSortOrder());

		verify(userRepository).getReferenceById(userId);
		verify(jobService).getBy(development, backendDeveloper);
		verify(userRepository).existsByNickname(nickname);
		verify(bookshelfService).updateJobIdByUserId(userId, backendJob.getId());
	}

	@DisplayName("changeProfile - 닉네임 중복 예외가 발생한다. ")
	@Test
	void changeProfile_throw() {
		//given
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);

		Job job = JobObjectProvider.backendJob();

		String changeNickname = "nickname";
		Nickname nickname = new Nickname(changeNickname);
		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(job.getJobGroup(),
				job.getJobName()));

		given(userRepository.getReferenceById(userId))
			.willReturn(user);

		given(userRepository.existsByNickname(nickname))
			.willReturn(true);

		//when
		assertThrows(DuplicateException.class, () -> defaultUserService.changeProfile(userId, request));

		verify(userRepository).getReferenceById(userId);
		verify(userRepository).existsByNickname(nickname);
	}

	@DisplayName("existsNickname - 닉네임이 존재하면 true 존재하지 않으면 false")
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void existsNickname(boolean exists) {
		//given
		String nicknameStr = "nickname";
		Nickname nickname = new Nickname(nicknameStr);
		given(userRepository.existsByNickname(nickname))
			.willReturn(exists);

		//when
		boolean existsNickname = defaultUserService.existsNickname(nickname);

		//then
		assertEquals(existsNickname, exists);
		verify(userRepository).existsByNickname(nickname);
	}

	@DisplayName("changeNickname - 유저 이름 변경에 성공한다. ")
	@Test
	void changeNickname_success() {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();

		Nickname nickname = new Nickname("changeName");

		given(userRepository.existsByNickname(nickname))
			.willReturn(false);

		given(userRepository.getReferenceById(userId))
			.willReturn(kakaoUser);

		//when
		defaultUserService.changeNickname(userId, nickname);

		//then
		verify(userRepository).existsByNickname(nickname);
		verify(userRepository).getReferenceById(userId);
		assertEquals(kakaoUser.getNickname(), nickname);
	}

	@DisplayName("changeNickname - 유저 이름이 중복이므로 변경에 실패한다. ")
	@Test
	void changeNickname_fail() {
		//given
		Long userId = 1L;
		Nickname nickname = new Nickname("nickname");

		willThrow(new DuplicateNicknameException())
			.given(userRepository).existsByNickname(nickname);

		//when
		assertThrows(DuplicateNicknameException.class,
			() ->
				defaultUserService.validateExistsNickname(nickname));
		//then
		verify(userRepository).existsByNickname(nickname);
	}

	@DisplayName("changeNickname - 이름 변경중 예외가 발생하여 변경에 실패한다. ")
	@Test
	void changeNickname_badNickname_fail() throws Exception {
		//given
		Long userId = 1L;
		User kakaoSpyUser = UserObjectProvider.createKakaoUser();


		kakaoSpyUser = spy(kakaoSpyUser);

		Nickname nickname = new Nickname("changeName");

		given(userRepository.existsByNickname(nickname))
			.willReturn(false);

		given(userRepository.getReferenceById(userId))
			.willReturn(kakaoSpyUser);

		willThrow(DataIntegrityViolationException.class)
			.given(kakaoSpyUser).changeNickname(nickname);

		//when
		assertThrows(DuplicateNicknameException.class,
			() ->
				defaultUserService.changeNickname(userId, nickname));
		//then
		verify(userRepository).existsByNickname(nickname);
		verify(userRepository).getReferenceById(userId);
		verify(kakaoSpyUser).changeNickname(nickname);
	}

}