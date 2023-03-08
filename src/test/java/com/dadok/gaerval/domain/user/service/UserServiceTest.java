package com.dadok.gaerval.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
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
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.integration_util.ServiceIntegration;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@Transactional
class UserServiceTest extends ServiceIntegration {

	private User kakaoUser;
	private OAuth2Attribute oAuth2Attribute = UserObjectProvider.kakaoAttribute();
	private Job backendJob;

	@BeforeEach
	void setUp() {
		Authority authority = authorityRepository.getReferenceById(Role.USER);
		UserAuthority userAuthority = UserAuthority.create(authority);
		User kakaoUser = User.createByOAuth(oAuth2Attribute, userAuthority);
		kakaoUser.changeNickname(new Nickname("kakaoUser"));
		backendJob = jobRepository.findByJobGroupAndJobName(JobGroup.DEVELOPMENT, JobGroup.JobName.BACKEND_DEVELOPER)
			.get();
		this.kakaoUser = userRepository.save(kakaoUser);
		kakaoUser.changeJob(backendJob);
		Bookshelf bookshelf = Bookshelf.create(kakaoUser);
		bookshelfRepository.save(bookshelf);
	}

	@DisplayName("authority를 찾아 유저를 저장하고 반환한다 - 성공")
	@Test
	void findByEmailWithAuthorities() {
		//given
		String email = kakaoUser.getEmail();
		//when
		Optional<User> userOptional = userService.findByEmailWithAuthorities(email);
		//then
		assertTrue(userOptional.isPresent());
		User findUser = userOptional.get();

		assertThat(findUser)
			.hasFieldOrPropertyWithValue("email", email);
		assertThat(findUser.getAuthorities()).hasSize(1);
	}

	@DisplayName("User 권한의 authority를 찾아 유저를 저장하고 반환한다 - 성공")
	@Test
	void register_success() {
		//given
		OAuth2Attribute oAuth2Attribute = UserObjectProvider.naverAttribute();
		//when
		User registerUser = userService.register(oAuth2Attribute);
		Optional<Bookshelf> bookshelfOptional = bookshelfRepository.findByUserId(registerUser.getId());
		//then
		assertNotNull(registerUser.getId());
		assertEquals(1, registerUser.getAuthorities().size());
		assertEquals(Role.USER, registerUser.getAuthorities().get(0).getAuthority().getName());
		assertEquals(AuthProvider.NAVER, registerUser.getAuthProvider());
		assertTrue(bookshelfOptional.isPresent());
		Bookshelf findBookShelf = bookshelfOptional.get();
		assertEquals(findBookShelf.getUser(), registerUser);
		assertNull(findBookShelf.getJobId());
		assertEquals(findBookShelf.getName(), registerUser.getOauthNickname() + "님의 책장");
	}

	@DisplayName("userId에 해당하는 user가 없으면 예외를 던진다")
	@Test
	void getById_throw() {
		//given
		Long userId = 123401238489238L;
		//when
		assertThrows(ResourceNotfoundException.class, () -> userService.getById(userId));
	}

	@DisplayName("user의 정보를 반환한다.")
	@Test
	void getUserProfile() {
		//given
		Long userId = kakaoUser.getId();
		JobGroup.JobName jobName = backendJob.getJobName();
		JobGroup jobGroup = backendJob.getJobGroup();
		//when
		UserProfileResponse userProfile = userService.getUserProfile(userId);
		//then
		assertThat(userProfile)
			.hasFieldOrPropertyWithValue("userId", userId)
			.hasFieldOrPropertyWithValue("nickname", kakaoUser.getNickname().nickname())
			.hasFieldOrPropertyWithValue("profileImage", kakaoUser.getProfileImage())
			.hasFieldOrPropertyWithValue("gender", kakaoUser.getGender());
		UserDetailResponse.JobDetailResponse userJob = userProfile.job();
		assertThat(userJob)
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", jobGroup.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", jobGroup)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", jobName.getJobName())
			.hasFieldOrPropertyWithValue("jobName", jobName)
			.hasFieldOrPropertyWithValue("order", backendJob.getSortOrder());
	}

	@DisplayName("존재하지 않는 유저를 찾으면 예외를 던진다")
	@Test
	void getUserProfile_throw() {
		//given
		Long userId = 123401238489238L;
		//when
		assertThrows(ResourceNotfoundException.class, () -> userService.getUserProfile(userId));
	}

	@DisplayName("getUserDetail - 유저가 존재하지 않으면 예외를 던진다.")
	@Test
	void getUserDetail_throw() {
		//given
		Long userId = 123401238489238L;
		//when
		assertThrows(ResourceNotfoundException.class, () -> userService.getUserDetail(userId));
	}

	@DisplayName("user의 정보를 반환한다.")
	@Test
	void getUserDetail() {
		//given
		Long userId = kakaoUser.getId();
		JobGroup.JobName jobName = backendJob.getJobName();
		JobGroup jobGroup = backendJob.getJobGroup();
		//when
		UserDetailResponse userDetailResponse = userService.getUserDetail(userId);
		//then
		assertThat(userDetailResponse)
			.hasFieldOrPropertyWithValue("userId", userId)
			.hasFieldOrPropertyWithValue("nickname", kakaoUser.getNickname().nickname())
			.hasFieldOrPropertyWithValue("profileImage", kakaoUser.getProfileImage())
			.hasFieldOrPropertyWithValue("gender", kakaoUser.getGender())
			.hasFieldOrPropertyWithValue("name", kakaoUser.getName())
			.hasFieldOrPropertyWithValue("oauthNickname", kakaoUser.getOauthNickname())
			.hasFieldOrPropertyWithValue("email", kakaoUser.getEmail())
			.hasFieldOrPropertyWithValue("authProvider", kakaoUser.getAuthProvider());

		UserDetailResponse.JobDetailResponse userJob = userDetailResponse.job();
		assertThat(userJob)
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", jobGroup.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", jobGroup)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", jobName.getJobName())
			.hasFieldOrPropertyWithValue("jobName", jobName)
			.hasFieldOrPropertyWithValue("order", backendJob.getSortOrder());
	}

	@DisplayName("유저의 직업이 백엔드에서 프론트로 바뀐다. 책장의 Job도 같이 바뀐다")
	@Test
	void changeJob() {
		//given
		Long userId = kakaoUser.getId();
		Job beforeJob = kakaoUser.getJob();
		Job frontendJob = JobObjectProvider.frontendJob();
		JobGroup frontendJobGroup = frontendJob.getJobGroup();
		JobGroup.JobName frontendJobName = frontendJob.getJobName();
		UserJobChangeRequest userJobChangeRequest = new UserJobChangeRequest(frontendJobGroup,
			frontendJobName);
		//when
		UserJobChangeResponse userJobChangeResponse = userService.changeJob(userId, userJobChangeRequest);
		UserDetailResponse.JobDetailResponse changedJob = userJobChangeResponse.job();

		//then
		User jobChangedUser = userRepository.getReferenceById(userId);
		Bookshelf bookshelf = bookshelfRepository.findByUserId(userId).get();

		assertThat(userJobChangeResponse)
			.hasFieldOrPropertyWithValue("userId", userId);
		assertThat(changedJob)
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", frontendJobGroup.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", frontendJobGroup)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", frontendJobName.getJobName())
			.hasFieldOrPropertyWithValue("jobName", frontendJobName)
			.hasFieldOrPropertyWithValue("order", frontendJob.getSortOrder());
		assertFalse(jobChangedUser.isSameJob(beforeJob));

		assertEquals(bookshelf.getJobId(), frontendJob.getId());
	}

	@DisplayName("프로필 닉네임, 잡 전체 변경에 성공한다.")
	@Test
	void changeProfile_success() {
		//given
		Long userId = kakaoUser.getId();
		Nickname beforeNickname = kakaoUser.getNickname();
		Job beforeJob = kakaoUser.getJob();
		Job frontendJob = JobObjectProvider.frontendJob();
		JobGroup frontendJobGroup = frontendJob.getJobGroup();
		JobGroup.JobName frontendJobName = frontendJob.getJobName();

		String changeNickname = "changed";

		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(frontendJobGroup,
				frontendJobName));
		//when
		UserDetailResponse userDetailResponse = userService.changeProfile(userId, request);

		//then
		User changedUser = userRepository.findById(userId).get();
		Bookshelf bookshelf = bookshelfRepository.findByUserId(userId).get();

		assertThat(userDetailResponse)
			.hasFieldOrPropertyWithValue("userId", userId)
			.hasFieldOrPropertyWithValue("nickname", changeNickname)
			.hasFieldOrPropertyWithValue("oauthNickname", kakaoUser.getOauthNickname())
			.hasFieldOrPropertyWithValue("email", kakaoUser.getEmail())
			.hasFieldOrPropertyWithValue("profileImage", kakaoUser.getProfileImage())
			.hasFieldOrPropertyWithValue("gender", kakaoUser.getGender())
			.hasFieldOrPropertyWithValue("authProvider", kakaoUser.getAuthProvider());

		assertThat(userDetailResponse.job())
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", frontendJobGroup.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", frontendJobGroup)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", frontendJobName.getJobName())
			.hasFieldOrPropertyWithValue("jobName", frontendJobName)
			.hasFieldOrPropertyWithValue("order", frontendJob.getSortOrder());

		assertFalse(kakaoUser.isSameJob(beforeJob));
		assertEquals(bookshelf.getJobId(), frontendJob.getId());
		Nickname nickname = changedUser.getNickname();
		assertNotEquals(beforeNickname, nickname);
	}

	@DisplayName("프로필 닉네임 만 변경에 성공한다.")
	@Test
	void changeProfile_nickname_success() {
		//given
		Long userId = kakaoUser.getId();
		Nickname beforeNickname = kakaoUser.getNickname();
		Job beforeJob = kakaoUser.getJob();
		JobGroup jobGroup = beforeJob.getJobGroup();
		JobGroup.JobName jobName = beforeJob.getJobName();

		String changeNickname = "changed";

		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(jobGroup,
				jobName));
		//when
		UserDetailResponse userDetailResponse = userService.changeProfile(userId, request);

		//then
		Optional<User> userOptional = userRepository.findById(userId);
		assertTrue(userOptional.isPresent());
		User changedUser = userOptional.get();
		Bookshelf bookshelf = bookshelfRepository.findByUserId(userId).get();

		assertThat(userDetailResponse)
			.hasFieldOrPropertyWithValue("userId", userId)
			.hasFieldOrPropertyWithValue("nickname", changeNickname)
			.hasFieldOrPropertyWithValue("oauthNickname", kakaoUser.getOauthNickname())
			.hasFieldOrPropertyWithValue("email", kakaoUser.getEmail())
			.hasFieldOrPropertyWithValue("profileImage", kakaoUser.getProfileImage())
			.hasFieldOrPropertyWithValue("gender", kakaoUser.getGender())
			.hasFieldOrPropertyWithValue("authProvider", kakaoUser.getAuthProvider());

		assertThat(userDetailResponse.job())
			.hasFieldOrPropertyWithValue("jobGroupKoreanName", jobGroup.getGroupName())
			.hasFieldOrPropertyWithValue("jobGroupName", jobGroup)
			.hasFieldOrPropertyWithValue("jobNameKoreanName", jobName.getJobName())
			.hasFieldOrPropertyWithValue("jobName", jobName)
			.hasFieldOrPropertyWithValue("order", beforeJob.getSortOrder());

		assertTrue(kakaoUser.isSameJob(beforeJob));
		assertEquals(bookshelf.getJobId(), beforeJob.getId());
		String nickname = changedUser.getNickname().nickname();
		assertNotEquals(nickname, beforeNickname.nickname());
	}

	@DisplayName("닉네임이 존재하면, 중복 예외가 발생한다. ")
	@Test
	void changeProfile_nickname_throw() {
		//given
		Long userId = kakaoUser.getId();
		Job beforeJob = kakaoUser.getJob();
		JobGroup jobGroup = beforeJob.getJobGroup();
		JobGroup.JobName jobName = beforeJob.getJobName();

		String existsNickname = "user1";

		UserChangeProfileRequest request = new UserChangeProfileRequest(existsNickname,
			new UserJobChangeRequest(jobGroup,
				jobName));
		//when
		assertThrows(DuplicateNicknameException.class,
			() -> userService.changeProfile(userId, request));

	}

	@DisplayName("닉네임이 존재하면 true 존재하지 않으면 false를 반환한다.")
	@Test
	void existsNickname() {
		//given
		User savedUser = kakaoUser;
		Nickname nickname = savedUser.getNickname();
		//when
		assertTrue(userService.existsNickname(nickname));
	}

	@DisplayName("유저 이름 변경에 성공한다. ")
	@Test
	void changeNickname_success() {
		//given
		Long userId = kakaoUser.getId();
		String beforeName = kakaoUser.getNickname().nickname();
		String changeNickname = "change";
		//when
		userService.changeNickname(userId, new Nickname(changeNickname));
		//then
		User changedUser = userRepository.getReferenceById(userId);
		assertEquals(changeNickname, changedUser.getNickname().nickname());
		assertNotEquals(beforeName, changedUser.getNickname().nickname());
	}

}