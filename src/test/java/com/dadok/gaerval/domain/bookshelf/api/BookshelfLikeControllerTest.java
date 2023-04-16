package com.dadok.gaerval.domain.bookshelf.api;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.dadok.gaerval.controller.ControllerSliceTest;
import com.dadok.gaerval.domain.bookshelf.service.BookshelfLikeService;
import com.dadok.gaerval.testutil.WithMockCustomOAuth2LoginUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@WebMvcTest(controllers = BookshelfLikeController.class)
@WithMockCustomOAuth2LoginUser
class BookshelfLikeControllerSliceTest extends ControllerSliceTest {

	@MockBean
	private BookshelfLikeService bookshelfLikeService;

	@DisplayName("createLike - 책장 좋아요 생성")
	@Test
	void createLike() throws Exception {
		// Given
		doNothing().when(bookshelfLikeService).createBookshelfLike(any(), eq(2L));

		// When // Then
		mockMvc.perform(post("/api/bookshelves/{bookshelvesId}/like", 2L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id")
				)
			))
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION))
					.pathParameters(
						parameterWithName("bookshelvesId").description("첵장 Id")
					)
					.build()
				)));
	}

	@DisplayName("deleteLike - 책장 좋아요 삭제")
	@Test
	void deleteLike() throws Exception {
		// Given
		doNothing().when(bookshelfLikeService).deleteBookshelfLike(any(), eq(2L));

		// When // Then
		mockMvc.perform(delete("/api/bookshelves/{bookshelvesId}/like", 2L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(ACCESS_TOKEN_HEADER_NAME, MOCK_ACCESS_TOKEN)
				.characterEncoding(StandardCharsets.UTF_8)
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(this.restDocs.document(
				requestHeaders(
					headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION)
				),
				pathParameters(
					parameterWithName("bookshelvesId").description("첵장 Id")
				)
			))
			.andDo(MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
				ResourceDocumentation.resource(ResourceSnippetParameters.builder()
					.requestHeaders(
						headerWithName(ACCESS_TOKEN_HEADER_NAME).description(ACCESS_TOKEN_HEADER_NAME_DESCRIPTION))
					.pathParameters(
						parameterWithName("bookshelvesId").description("첵장 Id")
					)
					.build()
				)));
	}
}