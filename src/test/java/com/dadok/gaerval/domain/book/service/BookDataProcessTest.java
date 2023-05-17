package com.dadok.gaerval.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dadok.gaerval.domain.book.converter.BookMapper;
import com.dadok.gaerval.domain.book.dto.response.OriginalBookData;
import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.global.config.externalapi.BookApiProvider;
import com.dadok.gaerval.testutil.BookObjectProvider;

@ExtendWith(SpringExtension.class)
class BookDataProcessTest {

	private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

	@DisplayName("일반적이지 않은 데이터의 전처리에 성공한다.")
	@ParameterizedTest
	@MethodSource("provideStringsForContents")
	 void processInvalid_success(String contents) {
		// given
		String title = "";
		List<String> authors = new ArrayList<>();
		String isbn = "1234567891";
		String publisher = "";
		String url = BookObjectProvider.url;
		String imageUrl = BookObjectProvider.imageUrl;

		// when
		Book weirdBook = bookMapper.originalBookDataToEntity(
			new OriginalBookData(title, authors, contents,isbn, url, imageUrl, publisher, BookApiProvider.KAKAO));

		// then
		assertEquals("책 제목 미상", weirdBook.getTitle());
		assertEquals("저자 미상", weirdBook.getAuthor());
		assertTrue(weirdBook.getContents().length() < 2000);
		assertEquals("출판사 미상", weirdBook.getPublisher());
	}


	private static Stream<Arguments> provideStringsForContents() {
		return Stream.of(
			Arguments.of(null, true),
			Arguments.of("", true),
			Arguments.of("  ", true),
			Arguments.of(StringUtils.repeat("a", 1999), false)
		);
	}

}