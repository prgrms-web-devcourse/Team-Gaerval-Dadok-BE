package com.dadok.gaerval.controller;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.dadok.gaerval.controller.document.utils.CustomResponseFieldsSnippet;
import com.fasterxml.jackson.core.type.TypeReference;

@TestPropertySource(value = "classpath:application-test.yml")
@WebMvcTest(controllers = EnumDocsController.class)
public class EnumDocsControllerTest extends ControllerSliceTest {

	private static final String ENUM_SNIPPET_FILE = "enum-response";

	/**
	 * (1)
	 * 앞서 작성한 커스텀 템플릿을 사용하도록 하는 코드입니다.
	 * 첫번째 인자 type
	 * - custom-response-fields.snippet 템플릿을 사용할 것이므로 "enum-response" 를 인자로 넘깁니다.
	 * 두번째 인자 subsectionExtractor
	 * - 현재 컨트롤러는 요청에 대한 응답으로 EnumResponse 객체를 보냅니다. 앞서 코드에서 반환값으로 EnumResponse는 data필드를 가지고 있고 이 데이터 필드 안에 문서화하고자 하는 enum값을 담아서 보냈습니다.
	 * - gender값을 예로 들면, data.gender에 값이 들어있습니다. 따라서 beneathPath에는 data.gender, withSubsectionId에는 gender를 명시해주면 이에 따라 데이터를 추출합니다.
	 * 세번째 인자 attributes
	 * - 속성값을 넣는 곳인데 이 부분은 아래서 볼 문서화 과정에서 보시는게 훨씬 이해하기 편할 것 같습니다.
	 * 네번째 인자 descriptors
	 * - 요청에 대한 응답값을 파싱해서 enumDocs를 추출해내면 이 안에는 Map 형태로 enum값들이 들어가 있습니다.
	 * - 이 값들을 문서화애 사용하기 위해 enumConvertFieldDescriptor 함수를 만들어 enum값들을 추출하여 FieldDescriptor로 만들어 인자로 넣어줍니다.
	 *
	 * @throws Exception
	 */
	@Test
	void enums() throws Exception {
		// 요청
		ResultActions result = this.mockMvc.perform(
			get("/test/enums")
				.contentType(MediaType.APPLICATION_JSON)
		);

		// 결과값
		MvcResult mvcResult = result.andReturn();

		// 데이터 파싱
		EnumDocs enumDocs = getData(mvcResult);

		CustomResponseFieldsSnippet customResponseFieldsSnippet = customResponseFields(ENUM_SNIPPET_FILE,
			beneathPath("data.jobGroup").withSubsectionId("jobGroup"),
			attributes(key("title").value("jobGroup")),
			enumConvertFieldDescriptor(enumDocs.jobGroup())
		);
		// 문서화 진행
		result.andDo(restDocs.document(
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.jobGroup").withSubsectionId("jobGroup"),
					attributes(key("title").value("jobGroup")),
					enumConvertFieldDescriptor(enumDocs.jobGroup())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.jobName").withSubsectionId("jobName"),
					attributes(key("title").value("jobName")),
					enumConvertFieldDescriptor(enumDocs.jobName())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.gender").withSubsectionId("gender"),
					attributes(key("title").value("gender")),
					enumConvertFieldDescriptor(enumDocs.gender())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.bookshelfItemType").withSubsectionId("bookshelfItemType"),
					attributes(key("title").value("bookshelfItemType")),
					enumConvertFieldDescriptor(enumDocs.bookshelfItemType())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.authProvider").withSubsectionId("authProvider"),
					attributes(key("title").value("provider")),
					enumConvertFieldDescriptor(enumDocs.authProvider())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.sortDirection").withSubsectionId("sortDirection"),
					attributes(key("title").value("sortDirection")),
					enumConvertFieldDescriptor(enumDocs.sortDirection())
				),
				customResponseFields(ENUM_SNIPPET_FILE,
					beneathPath("data.groupSearchOption").withSubsectionId("groupSearchOption"),
					attributes(key("title").value("groupSearchOption")),
					enumConvertFieldDescriptor(enumDocs.groupSearchOption())
				),
			customResponseFields(ENUM_SNIPPET_FILE,
				beneathPath("data.bestSellerSearchRange").withSubsectionId("bestSellerSearchRange"),
				attributes(key("title").value("bestSellerSearchRange")),
				enumConvertFieldDescriptor(enumDocs.bestSellerSearchRange())
			)
			)
		)
		;
	}

	// 커스텀 템플릿 사용을 위한 함수
	public static CustomResponseFieldsSnippet customResponseFields(
		String type,
		PayloadSubsectionExtractor<?> subsectionExtractor,
		Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
			, true);
	}

	// Map으로 넘어온 enumValue를 fieldWithPath로 변경하여 리턴
	private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
		return enumValues.entrySet().stream()
			.map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
			.toArray(FieldDescriptor[]::new);
	}

	// mvc result 데이터 파싱
	private EnumDocs getData(MvcResult result) throws IOException {
		EnumResponse<EnumDocs> enumResponse = objectMapper
			.readValue(result.getResponse().getContentAsByteArray(),
				new TypeReference<>() {
				}
			);
		return enumResponse.data();
	}

}
