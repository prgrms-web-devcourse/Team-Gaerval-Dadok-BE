package com.dadok.gaerval.domain.user.api;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.exception.DuplicateNicknameException;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WithMockCustomOAuth2LoginUser(userId = 1L)
@WebMvcTest(controllers = UserController.class)
class UserControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private UserService userService;

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("me - ?????? ???????????? ????????? ????????????.")
	@Test
	void me() throws Exception {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("???????????????"));
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
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("?????? ??????. ??????"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("?????? ?????????"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth??? ?????? ????????? ?????? ?????????"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("?????? ?????????"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("?????? ????????? url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("?????? ????????? : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth ?????????"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("??????"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).optional().description("?????? ?????????"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

					)

				)
			);

		//then
		verify(userService).getUserDetail(userId);
	}

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("userProfile - ?????? ?????? ????????? ????????????.")
	@Test
	void userProfile() throws Exception {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(kakaoUser, "nickname", new Nickname("???????????????"));
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
						fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("?????? ????????? url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).optional().description("?????? ????????? : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("??????"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

					)

				)
			);

		//then
		verify(userService).getUserProfile(userId);
	}

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("registerUserJob - ????????? ????????? ??????????????? ????????????.")
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
					pathParameters(parameterWithName("userId").description("userId. ????????? ?????? ????????? ???????????? 403")),

					requestFields(
						fieldWithPath("jobGroup").description("?????? ?????????. ???????????? ?????? :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobGroup")
							),
						fieldWithPath("jobName").description("?????? ?????????. ???????????? ?????? :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
							.attributes(
								constrainsAttribute(UserJobChangeRequest.class, "jobName")
							)
					),

					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("?????? user Id"),
						fieldWithPath("job").type(JsonFieldType.OBJECT).description("????????? ??????, ?????? ??????"),

						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING)
							.description("?????? ?????????"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING)
							.description("?????? ????????? :  " +
								DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING)
							.optional()
							.description("?????? ?????????"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

					)

				)
			);

		//then
		verify(userService).changeJob(userId, userJobChangeRequest);
	}

	@DisplayName("changeProfile - ?????? ????????? ????????? ????????????.")
	@Test
	void changeProfile_success() throws Exception {
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "nickname", new Nickname("????????????"));

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;
		Job backendJob = JobObjectProvider.backendJob();

		String changeNickname = "???????????????";

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
							.description("nickname. ??????????????? ????????? ????????? ??????, ??????, ?????? 2~10????????? ??????")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("??????"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					,
					responseFields(
						fieldWithPath("userId").type(JsonFieldType.NUMBER).description("user Id"),
						fieldWithPath("name").type(JsonFieldType.STRING).optional().description("?????? ??????. ??????"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("?????? ?????????"),
						fieldWithPath("oauthNickname").type(JsonFieldType.STRING).description("oauth??? ?????? ????????? ?????? ?????????"),

						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("?????? ?????????"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("?????? ????????? url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("?????? ????????? : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth ?????????"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).description("??????"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).description("?????? ?????????"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).description("?????? ?????? ??????")

					)

				)
			)
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

	@DisplayName("changeProfile - ?????? ????????? ????????? ????????????.")
	@Test
	void changeProfile_fail() throws Exception {
		User user = UserObjectProvider.createKakaoUser();
		Long userId = 1L;
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "nickname", new Nickname("????????????"));

		JobGroup development = JobGroup.DEVELOPMENT;
		JobGroup.JobName backendDeveloper = JobGroup.JobName.BACKEND_DEVELOPER;

		String changeNickname = "???????????????";

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
							.description("nickname. ??????????????? ????????? ????????? ??????, ??????, ?????? 2~10????????? ??????")
							.attributes(
								constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							),
						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("??????"),
						fieldWithPath("job.jobGroup").type(JsonFieldType.STRING).optional().description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("?????? ????????? :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
					)

					,
					responseFields(
						fieldWithPath("status").type(JsonFieldType.NUMBER).description("httpStatus"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
						fieldWithPath("timestamp").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("errors").type(JsonFieldType.STRING).optional().description("?????? ?????????"),
						fieldWithPath("code").type(JsonFieldType.STRING).description("?????? ??????"),
						fieldWithPath("path").type(JsonFieldType.STRING).description("?????? URL")
					)

				)
			)

		;
		//then
		verify(userService).changeProfile(userId, request);
	}

	@DisplayName("existsUsername - ?????? ???????????? ???????????? true??? ????????????.")
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
							"?????? ?????? ????????? nickname. \n ??????????????? ????????? ????????? ??????, ??????, ?????? 2~10????????? ??????")
						.attributes(key("constraints").value("""
							Must match the regular expression \n
							`^[???-???0-9a-zA-Z]{2,10}$`\s
									
							Must not be blank""")
						)
					)

					,
					responseFields(
						fieldWithPath("isExists").description("?????? ????????? ?????? ??????. ???????????? true ????????? false")
					)
				)
			);

		//then
		verify(userService).existsNickname(nickname);
	}

	@DisplayName("changeNickname - ?????? ?????? ????????? ????????????. ")
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
							.description("????????? ??????. ")
							.attributes(constrainsAttribute(UserChangeProfileRequest.class, "nickname")
							)
					)
				)
			);

		//then
		verify(userService).changeNickname(userId, nickname);
	}

	@DisplayName("changeNickname - ?????? ????????? ??????????????? ????????? ????????????. ")
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

	@DisplayName("changeNickname - ?????? ????????? ????????? ??????????????? ????????? ????????????. ")
	@Test
	void changeNickname_badNickname_fail() throws Exception {
		//given

		mockMvc.perform(patch("/api/users/profile/nickname")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			.content("{\"nickname\":\" s????????????0?????? \"}")
		).andExpect(status().isBadRequest());

	}

}