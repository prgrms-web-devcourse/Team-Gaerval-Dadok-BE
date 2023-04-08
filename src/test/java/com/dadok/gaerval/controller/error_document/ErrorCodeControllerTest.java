package com.dadok.gaerval.controller.error_document;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.controller.document.utils.CustomResponseFieldsSnippet;
import com.dadok.gaerval.global.error.ErrorCode;

@TestPropertySource(value = "classpath:application-test.yml")
@WebMvcTest(controllers = ErrorCodeController.class)
class ErrorCodeControllerTest extends ControllerSliceTest {

	private static final String ERROR_SNIPPET_FILE = "errorcode-response";

	@Test
	void errorCodes() throws Exception {
		ResultActions result = mockMvc.perform(get("/error-code")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		result.andDo(document(ERROR_SNIPPET_FILE,
			customResponseFields(ERROR_SNIPPET_FILE,
				fieldDescriptors()
			)
		));
	}

	private FieldDescriptor[] fieldDescriptors() {
		List<FieldDescriptor> fieldDescriptors = new ArrayList<>();

		for (ErrorCode errorCode : ErrorCode.values()) {
			FieldDescriptor attributes =
				fieldWithPath(errorCode.name()).type(JsonFieldType.OBJECT)
					.attributes(key("code").value(errorCode.getCode()),
						key("message").value(errorCode.getMessage()),
						key("statusCode").value(String.valueOf(errorCode.getStatus().value())),
						key("status").value(errorCode.getStatus().getReasonPhrase()));
			fieldDescriptors.add(attributes);
		}

		return fieldDescriptors.toArray(FieldDescriptor[]::new);
	}

	public static CustomResponseFieldsSnippet customResponseFields(
		String snippetFilePrefix,
		FieldDescriptor... descriptors) {
		return new CustomResponseFieldsSnippet(snippetFilePrefix, Arrays.asList(descriptors), true);
	}

}