package com.dadok.gaerval.domain.user.api;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.dto.request.UserChangeProfileRequest;
import com.dadok.gaerval.domain.user.dto.request.UserJobChangeRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobChangeResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
import com.dadok.gaerval.domain.user.entity.Gender;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.exception.DuplicateNicknameException;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@WithMockCustomOAuth2LoginUser(userId = 1L)
@WebMvcTest(controllers = UserController.class)
class UserControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private UserService userService;

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("me - 유저 개인정보 조회에 성공한다.")
	@Test
	void me() throws Exception {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("임시닉네임"));
		UserDetailResponse mockUserDetailResponse = new UserDetailResponse(userId, kakaoUser.getName(),
			kakaoUser.getNickname().nickname(), kakaoUser.getOauthNickname(), kakaoUser.getEmail(),
			kakaoUser.getProfileImage(), kakaoUser.getGender(), kakaoUser.getAuthProvider(), JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER, 1);

		given(userService.getUserDetail(userId))
			.willReturn(mockUserDetailResponse);

		//when
		mockMvc.perform(get("/api/users/me")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("유저 이름. 실명"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("유저 닉네임"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth를 통해 가입한 유저 닉네임"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("유저 이메일"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth 제공자"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).optional().description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)

				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					)
					.responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("유저 이름. 실명"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("유저 닉네임"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth를 통해 가입한 유저 닉네임"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("유저 이메일"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth 제공자"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
							.optional()
							.description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)

					.build()
				)))
		;

		//then
		verify(userService).getUserDetail(userId);
	}

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("userProfile - 유저 정보 조회에 성공한다.")
	@Test
	void userProfile() throws Exception {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("임시닉네임"));
		var mockUserProfileResponse = new UserProfileResponse(userId,
			kakaoUser.getNickname().nickname(), kakaoUser.getProfileImage(), kakaoUser.getGender(),
			JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER, 1);

		given(userService.getUserProfile(userId))
			.willReturn(mockUserProfileResponse);

		//when
		mockMvc.perform(get("/api/users/{userId}/profile", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(parameterWithName("userId").description("user Id")),
					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).optional().description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)

				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					)
					.pathParameters(parameterWithName("userId").description("user Id"))
					.responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).optional().description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)
					.build()
				)))
		;

		//then
		verify(userService).getUserProfile(userId);
	}

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("registerUserJob - 유저의 직업을 변경하는데 성공한다.")
	@Test
	void registerUserJob() throws Exception {
		//given
		Long userId = 1L;

		Job backendJob = JobObjectProvider.backendJob();

		UserJobChangeRequest userJobChangeRequest = new UserJobChangeRequest(JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER);

		UserJobChangeResponse userJobChangeResponse = new UserJobChangeResponse(userId,
			new UserDetailResponse.JobDetailResponse(backendJob.getJobGroup(), backendJob.getJobName(),
				backendJob.getSortOrder()));

		given(userService.changeJob(userId, userJobChangeRequest))
			.willReturn(userJobChangeResponse);

		//when
		mockMvc.perform(patch("/api/users/{userId}/jobs", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(createJson(userJobChangeRequest))
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					pathParameters(parameterWithName("userId").description("userId. 본인이 아닌 사람이 요청하면 403")),

					requestFields(
						fieldWithPath("jobGroup").description("직군 영어명. 대문자로 요청 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobGroup")
							),
						fieldWithPath("jobName").description("직업 영어명. 대문자로 요청 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobName")
							)
					),

					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("요청 user Id"),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("등록된 직군, 직업 정보"),

						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
							.description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
							.description("직군 영어명 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
							.optional()
							.description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)

				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					)
					.pathParameters(parameterWithName("userId").description("userId. 본인이 아닌 사람이 요청하면 403"))

					.requestFields(
						fieldWithPath("jobGroup").description("직군 영어명. 대문자로 요청 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobGroup")
							),
						fieldWithPath("jobName").description("직업 영어명. 대문자로 요청 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobName")
							)
					)

					.responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("요청 user Id"),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("등록된 직군, 직업 정보"),

						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
							.description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
							.description("직군 영어명 :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
							.optional()
							.description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)
					.build()
				)))
		;

		//then
		verify(userService).changeJob(userId, userJobChangeRequest);
	}

	@DisplayName("changeProfile - 유저 프로필 변경에 성공한다.")
	@Test
	void changeProfile_success() throws Exception {
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "nickname", new Nickname("기존이름"));

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;
		Job backendJob = JobObjectProvider.backendJob();

		String changeNickname = "변경된이름";

		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(development,
				backendDeveloper));

		UserDetailResponse userDetailResponse = new UserDetailResponse(user.getId(), user.getName(),
			changeNickname,
			user.getOauthNickname(), user.getEmail(), user.getProfileImage(), user.getGender(),
			user.getAuthProvider(),
			new UserDetailResponse.JobDetailResponse(backendJob.getJobGroup(), backendJob.getJobName(),
				backendJob.getSortOrder()));

		given(userService.changeProfile(userId, request))
			.willReturn(userDetailResponse);

		//when
		mockMvc.perform(put("/api/users/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(createJson(request))
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					),

					requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("nickname. 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("직업"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					,
					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("유저 이름. 실명"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("유저 닉네임"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth를 통해 가입한 유저 닉네임"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("유저 이메일"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth 제공자"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)

				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					)

					.requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("nickname. 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("직업"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					.responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("유저 이름. 실명"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("유저 닉네임"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth를 통해 가입한 유저 닉네임"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("유저 이메일"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth 제공자"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)
					.build()
				)))
			.andExpect(jsonPath("userId").value(1L))
			.andExpect(jsonPath("name").doesNotExist())
			.andExpect(jsonPath("nickname").value(changeNickname))
			.andExpect(jsonPath("oauthNickname").value(user.getOauthNickname()))
			.andExpect(jsonPath("email").value(user.getEmail()))
			.andExpect(jsonPath("profileImage").value(user.getProfileImage()))
			.andExpect(jsonPath("gender").value(user.getGender().name()))
			.andExpect(jsonPath("authProvider").value(user.getAuthProvider().name()))
			.andExpect(jsonPath("$.job.jobGroupKoreanName").value(backendJob.getJobGroup().getGroupName()))
			.andExpect(jsonPath("$.job.jobGroupName").value(backendJob.getJobGroup().name()))
			.andExpect(jsonPath("$.job.jobName").value(backendJob.getJobName().name()))
			.andExpect(jsonPath("$.job.jobNameKoreanName").value(backendJob.getJobName().getJobName()))
			.andExpect(jsonPath("$.job.order").value(backendJob.getSortOrder()))
		;
		//then
		verify(userService).changeProfile(userId, request);
	}

	@DisplayName("changeProfile - 유저 프로필 변경에 실패한다.")
	@Test
	void changeProfile_fail() throws Exception {
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "nickname", new Nickname("기존이름"));

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;

		String changeNickname = "변경된이름";

		UserChangeProfileRequest request = new UserChangeProfileRequest(changeNickname,
			new UserJobChangeRequest(development,
				backendDeveloper));

		DuplicateNicknameException duplicateNicknameException = new DuplicateNicknameException();

		given(userService.changeProfile(userId, request))
			.willThrow(duplicateNicknameException);

		//when
		mockMvc.perform(put("/api/users/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(createJson(request))
			)
			.andExpect(status().isBadRequest())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					),

					requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("nickname. 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("직업"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					,
					responseFields(
						fieldWithPath("status").type(JsonFieldType.NUMBER).description("httpStatus"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
						fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
						fieldWithPath("errors").type(JsonFieldType.STRING).optional().description("에러 필드들"),
						fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드"),
						fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL")
					)

				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					)

					.requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("nickname. 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("직업"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					.responseFields(
						fieldWithPath("status").type(JsonFieldType.NUMBER).description("httpStatus"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
						fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간"),
						fieldWithPath("errors").type(JsonFieldType.STRING).optional().description("에러 필드들"),
						fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드"),
						fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URL")
					)
					.build()
				)))
		;
		//then
		verify(userService).changeProfile(userId, request);
	}

	@DisplayName("existsUsername - 유저 닉네임이 존재하면 true를 반환한다.")
	@Test
	void existsUsername_true() throws Exception {
		//given
		Nickname nickname = new Nickname("nickname");

		given(userService.existsNickname(nickname))
			.willReturn(true);

		//when
		mockMvc.perform(get("/api/users/nickname")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.queryParam("nickname", "nickname")
			)
			.andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					),
					requestParameters(parameterWithName("nickname").description(
							"존재 여부 확인할 nickname. \n 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
						.attributes(key("constraints").value("""
							Must match the regular expression \n
							`^[가-힣0-9a-zA-Z]{2,10}$`\s
									
							Must not be blank""")
						)
					)

					,
					responseFields(
						fieldWithPath("isExists").description("유저 닉네임 존재 여부. 존재하면 true 아니면 false")
					)
				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					)
					.requestParameters(parameterWithName("nickname").description(
							"존재 여부 확인할 nickname. \n 특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용")
						.attributes(key("constraints").value("""
							Must match the regular expression \n
							`^[가-힣0-9a-zA-Z]{2,10}$`\s
									
							Must not be blank""")
						)
					)
					.responseFields(
						fieldWithPath("isExists").description("유저 닉네임 존재 여부. 존재하면 true 아니면 false")
					)
					.build()
				)))
		;

		//then
		verify(userService).existsNickname(nickname);
	}

	@DisplayName("changeNickname - 유저 이름 변경에 성공한다. ")
	@Test
	void changeNickname_success() throws Exception {
		//given
		Long userId = 1L;
		Nickname nickname = new Nickname("changeName");

		willDoNothing()
			.given(userService).changeNickname(userId, nickname);

		//when
		mockMvc.perform(patch("/api/users/profile/nickname")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(createJson(nickname))
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					),
					requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("변경할 이름. ")
							.attributes(constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							)
					)
				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION),
						headerWithName(HttpHeaders.ACCEPT).description(ACCEPT_JSON_DESCRIPTION)
					)
					.requestFields(
						fieldWithPath("nickname").type(JsonFieldType.STRING)
							.description("변경할 이름. ")
							.attributes(constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							)
					)
					.build()
				)))
		;

		//then
		verify(userService).changeNickname(userId, nickname);
	}

	@DisplayName("changeNickname - 유저 이름이 중복이므로 변경에 실패한다. ")
	@Test
	void changeNickname_fail() throws Exception {
		//given
		Long userId = 1L;
		Nickname nickname = new Nickname("nickname");

		willThrow(new DuplicateNicknameException())
			.given(userService).changeNickname(userId, nickname);

		//when
		mockMvc.perform(patch("/api/users/profile/nickname")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			.content(createJson(nickname))
		).andExpect(status().isBadRequest());

		//then
		verify(userService).changeNickname(userId, nickname);
	}

	@DisplayName("changeNickname - 유저 이름이 잘못된 형식이므로 변경에 실패한다. ")
	@Test
	void changeNickname_badNickname_fail() throws Exception {
		//given
		mockMvc.perform(patch("/api/users/profile/nickname")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			.content("{\"nickname\":\" sㅊㅂㄷㅇ0ㅏ래 \"}")
		).andExpect(status().isBadRequest());

	}

	@DisplayName("searchAllByNickname - 검색한 유저 닉네임을 포함한 유저 리스트를 반환한다 ")
	@Test
	void searchAllByNickname_success() throws Exception {
		//given
		String searchNickname = "다독";

		List<UserProfileResponse> userProfileResponses = UserProfileResponses(10, searchNickname);

		UserProfileResponses profileResponses = new UserProfileResponses(true, false, true, 10, false,
			userProfileResponses);

		given(userService.searchAllByNickname(new Nickname(searchNickname), 10))
			.willReturn(profileResponses);

		mockMvc.perform(get("/api/users/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.queryParam("nickname", searchNickname)
			).andExpect(status().isOk())
			.andDo(this.restDocs.document(
					requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					),
					requestParameters(
						parameterWithName("nickname").description("검색할 이름. ")
							.attributes(constrainsAttribute(UserChangeProfileRequest.class, "nickname")),
						parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
					),

					responseFields(
						fieldWithPath("count").description("유저 데이터 수").type(JsonFieldType.NUMBER),
						fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
						fieldWithPath("isFirst").description("첫 번째 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("isLast").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("hasNext").description("다음 데이터 존재 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("users[].userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("users[].nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
						fieldWithPath("users[].profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("users[].gender").type(JsonFieldType.STRING).optional().description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("users[].job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("users[].job.jobGroupKoreanName").type(JsonFieldType.STRING).description("직군 한글명"),
						fieldWithPath("users[].job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("users[].job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("users[].job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("users[].job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")

					)
				)
			)
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
						headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
					)
					.requestParameters(
						parameterWithName("nickname").description("검색할 이름. ")
							.attributes(constrainsAttribute(UserChangeProfileRequest.class, "nickname")),
						parameterWithName("pageSize").description("요청 데이터 수. default : 10").optional()
					)
					.responseFields(
						fieldWithPath("count").description("유저 데이터 수").type(JsonFieldType.NUMBER),
						fieldWithPath("isEmpty").description("데이터가 없으면 empty = true").type(JsonFieldType.BOOLEAN),
						fieldWithPath("isFirst").description("첫 번째 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("isLast").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("hasNext").description("다음 데이터 존재 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("users[].userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("users[].nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
						fieldWithPath("users[].profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("users[].gender").type(JsonFieldType.STRING).optional().description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("users[].job").type(JsonFieldType.OBJECT).description("직업"),
						fieldWithPath("users[].job.jobGroupKoreanName").type(JsonFieldType.STRING)
							.description("직군 한글명"),
						fieldWithPath("users[].job.jobGroupName").type(JsonFieldType.STRING).description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("users[].job.jobNameKoreanName").type(JsonFieldType.STRING).description("직업 한글명"),

						fieldWithPath("users[].job.jobName").type(JsonFieldType.STRING).description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("users[].job.order").type(JsonFieldType.NUMBER).description("직업 정렬 순위")
					)
					.build()
				)))

		;
	}

	private List<UserProfileResponse> UserProfileResponses(int size, String name) {

		return IntStream.range(0, size)
			.mapToObj(it -> new UserProfileResponse(
				(long)it,
				name + it,
				UserObjectProvider.PICTURE_URL + it,
				Gender.MALE, JobObjectProvider.randomJobDetailResponse()))
			.toList();
	}

}