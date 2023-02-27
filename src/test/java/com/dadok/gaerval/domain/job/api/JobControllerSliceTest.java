package com.dadok.gaerval.domain.job.api;

import static com.dadok.gaerval.global.config.security.jwt.JwtService.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.dadok.gaerval.controller.ControllerTest;
import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.domain.job.service.JobService;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;

@WebMvcTest(controllers = JobController.class)
class JobControllerSliceTest extends ControllerTest {

	@MockBean
	private JobService jobService;

	@WithMockCustomOAuth2LoginUser(userId = 1L)
	@DisplayName("직업 리스트 조회 - 직업 리스트를 정렬된 상태로 반환한다.")
	@Test
	void findAllJobList() throws Exception {
		//given

		given(jobService.findAllWithAsc())
			.willReturn(JobObjectProvider.mockData());

		//when
		mockMvc.perform(get("/api/jobs")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
			).andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION),
					headerWithName(HttpHeaders.CONTENT_TYPE).description(CONTENT_TYPE_JSON_DESCRIPTION)
				),
				responseFields(
					fieldWithPath("jobs").type(JsonFieldType.ARRAY).description("직군, 직업 목록"),
					fieldWithPath("jobs[].jobGroup").type(JsonFieldType.OBJECT).description("직군명"),
					fieldWithPath("jobs[].jobGroup.koreanName").type(JsonFieldType.STRING).description("직군 한글명"),
					fieldWithPath("jobs[].jobGroup.name").type(JsonFieldType.STRING).description("직군 영어명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_GROUP)),
					fieldWithPath("jobs[].jobNames").type(JsonFieldType.ARRAY).description("직업명"),
					fieldWithPath("jobs[].jobNames[].koreanName").type(JsonFieldType.STRING).description("직업 한글명"),
					fieldWithPath("jobs[].jobNames[].name").type(JsonFieldType.STRING).description("직군 영어명 :  " +
						DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.JOB_NAME)),
					fieldWithPath("jobs[].jobNames[].order").type(JsonFieldType.NUMBER).description("정렬 순서")

				)
			))
		;
	}

}