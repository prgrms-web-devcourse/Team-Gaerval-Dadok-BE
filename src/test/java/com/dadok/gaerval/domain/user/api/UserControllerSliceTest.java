package com.dadok.gaerval.domain.user.api;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.dto.request.UserJobRegisterRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobRegisterResponse;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WebMvcTest(controllers = UserController.class)
class UserControllerSliceTest extends ControllerTest {

	@MockBean
	private UserService userService;

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("me - 유저 개인정보 조회에 성공한다.")
	@Test
	void me() throws Exception {
		//given
		Long userId = 1L;
		User kakaoUser = UserObjectProvider.createKakaoUser();

		UserDetailResponse mockUserDetailResponse = new UserDetailResponse(userId, kakaoUser.getName(),
			kakaoUser.getNickname(), kakaoUser.getEmail(),
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
						fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
						fieldWithPath("email").type(JsonFieldType.STRING).optional().description("유저 이메일"),
						fieldWithPath("profileImage").type(JsonFieldType.STRING).description("유저 프로필 url"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 영어명 : " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.GENDER)
						),
						fieldWithPath("authProvider").type(JsonFieldType.STRING).description("OAuth 제공자"),

						fieldWithPath("job").type(JsonFieldType.OBJECT).optional().description("직업"),
						fieldWithPath("job.jobGroupKoreanName").type(JsonFieldType.STRING).optional().description("직군 한글명"),
						fieldWithPath("job.jobGroupName").type(JsonFieldType.STRING).optional().description("직군 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),

						fieldWithPath("job.jobNameKoreanName").type(JsonFieldType.STRING).optional().description("직업 한글명"),

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).optional().description("직업 정렬 순위")

					)

				)
			);

		//then
		verify(userService).getUserDetail(userId);
	}

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("registerUserJob - 유저의 직업을 변경하는데 성공한다.")
	@Test
	void registerUserJob() throws Exception {
		//given
		Long userId = 1L;

		Job backendJob = JobObjectProvider.backendJob();

		UserJobRegisterRequest userJobRegisterRequest = new UserJobRegisterRequest(JobGroup.DEVELOPMENT,
			JobGroup.JobName.BACKEND_DEVELOPER);

		UserJobRegisterResponse userJobRegisterResponse = new UserJobRegisterResponse(userId,
			new UserDetailResponse.JobDetailResponse(backendJob.getJobGroup(), backendJob.getJobName(),
				backendJob.getSortOrder()));

		given(userService.registerJob(userId, userJobRegisterRequest))
			.willReturn(userJobRegisterResponse);

		//when
		mockMvc.perform(patch("/api/users/{userId}/jobs", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.content(createJson(userJobRegisterRequest))
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
								constrainsAttribute(UserJobRegisterRequest.class, "jobGroup")
							),
						fieldWithPath("jobName").description("직업 영어명. 대문자로 요청 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME))
							.attributes(
								constrainsAttribute(UserJobRegisterRequest.class, "jobName")
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

						fieldWithPath("job.jobName").type(JsonFieldType.STRING).optional().description("직업 영어명 :  " +
							DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
						fieldWithPath("job.order").type(JsonFieldType.NUMBER).optional().description("직업 정렬 순위")

					)

				)
			);

		//then
		verify(userService).registerJob(userId, userJobRegisterRequest);
	}

}