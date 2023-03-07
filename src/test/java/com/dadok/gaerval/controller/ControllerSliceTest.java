package com.dadok.gaerval.controller;

import static org.springframework.restdocs.snippet.Attributes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.dadok.gaerval.controller.document.utils.RestDocsConfig;
import com.dadok.gaerval.infra.slack.SlackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Disabled
@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest
public abstract class ControllerSliceTest {

	public static final String MOCK_ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJibGFja2RvZyIsImF1dGgiOlsiUk9MRV9VU0VSIl0sImVtYWlsIjoiYmxhY2tkb2dAYmxhY2tkb2cuY29tIiwiZXhwIjoxNjc3NTE2MTk0fQ.vREr5l-2vzr962PAToOT3mGjOrn6HS3moFukH4fGO1OHLAciGoXZoMZ4NKFP_Kqr8YNCO0rXYh2rDVUWYtmQIg";

	public static final String CONTENT_TYPE_JSON_DESCRIPTION = "Content type 필수 :  application/json";

	public static final String ACCESS_TOKEN_HEADER_NAME_DESCRIPTION = "인증 형식 : Bearer ";
	public static final String ACCEPT_JSON_DESCRIPTION = "Accept type 필수 : accept";

	@MockBean
	private SlackService slackService;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

	protected String createJson(Object dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	@Autowired
	protected RestDocumentationResultHandler restDocs;

	@BeforeEach
	void setUp(final WebApplicationContext context,
		final RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider))  // rest docs 설정 주입
			.alwaysDo(MockMvcResultHandlers.print()) // andDo(print()) 코드 포함
			.alwaysDo(restDocs) // pretty 패턴과 문서 디렉토리 명 정해준것 적용
			.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
			.build();
	}

	public static String getConstraintMessage(Class<?> constraintClassType, String propertyName) {
		ConstraintDescriptions constraintDescriptions =
			new ConstraintDescriptions(constraintClassType);
		List<String> nameDescription = constraintDescriptions.descriptionsForProperty(propertyName);
		return String.join("\n", nameDescription);
	}

	public static Attributes.Attribute constrainsAttribute(Class<?> constraintClassType, String propertyName) {
		return key("constraints").value(getConstraintMessage(constraintClassType, propertyName));
	}

}
