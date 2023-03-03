package com.dadok.gaerval.oauth;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.dadok.gaerval.controller.document.utils.DocumentLinkGenerator;
import com.dadok.gaerval.controller.document.utils.RestDocsConfig;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.infra.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Import({RestDocsConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(value = OAuth2MockingController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class Oauth2Test {

	private final ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private final RestDocumentationResultHandler restDocs;

	private final ConverterRegistry converterRegistry;

	@MockBean
	private SlackService slackService;

	@BeforeEach
	void setUp(final WebApplicationContext context,
		final RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider))  // rest docs 설정 주입
			.alwaysDo(MockMvcResultHandlers.print()) // andDo(print()) 코드 포함
			.alwaysDo(restDocs) // pretty 패턴과 문서 디렉토리 명 정해준것 적용
			.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
			.build();

		converterRegistry.addConverter(new StringToAuthProviderConverter());
	}

	private static class StringToAuthProviderConverter implements Converter<String, AuthProvider> {

		@Override
		public AuthProvider convert(String source) {
			return AuthProvider.of(source.toUpperCase());
		}
	}

	@DisplayName("Oauth2 로그인 요청")
	@Test
	void oauth2Login() throws Exception {
		//given
		String redirect_uri = "https://localhost:3000/oauth/callback";
		//when
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("redirect_uri", redirect_uri);

		mockMvc.perform(get("/oauth2/authorize/{provider}",
			AuthProvider.KAKAO.name().toLowerCase(), redirect_uri)
			.params(headers)
		).andDo(this.restDocs.document(

			pathParameters(parameterWithName("provider").description("Oauth 제공자 Id. 소문자로 요청하셔야 합니다. : " +
				DocumentLinkGenerator.generateLinkCode(DocumentLinkGenerator.DocUrl.AUTH_PROVIDER))),

			requestParameters(parameterWithName("redirect_uri").description("인증 후 redirect 할 frontend uri")),

			responseHeaders(
				headerWithName("Location").description("요청시 사용한 redirect_uri, with accessToken")
			)
		));
	}

}
