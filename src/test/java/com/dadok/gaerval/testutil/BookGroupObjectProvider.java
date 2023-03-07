package com.dadok.gaerval.testutil;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponse;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public class BookGroupObjectProvider {

	public static BookGroup createMockBookGroup(Book book, Long ownerId) {
		BookGroup bookGroup = BookGroup.create(
			ownerId, book, LocalDate.now(), LocalDate.now(),
			5, "책읽기 소모임", "책읽기 소모임", true, "월든 작가는?",
			"헨리데이빗소로우", true, new BCryptPasswordEncoder());
		ReflectionTestUtils.setField(bookGroup, "id", 234L);
		return bookGroup;
	}

	public static BookGroup createBookGroup(Book book, Long ownerId) {
		BookGroup bookGroup = BookGroup.create(
			ownerId, book, LocalDate.now(), LocalDate.now(),
			5, "책읽기 소모임", "책읽기 소모임", true, "월든 작가는?",
			"헨리데이빗소로우", true, new BCryptPasswordEncoder());

		return bookGroup;
	}

	public static BookGroup createMockBookGroup(Book book, Long ownerId, PasswordEncoder passwordEncoder) {
		BookGroup bookGroup = BookGroup.create(
			ownerId, book, LocalDate.now(), LocalDate.now(),
			5, "책읽기 소모임", "책읽기 소모임", true, "월든 작가는?",
			"헨리데이빗소로우", true, passwordEncoder);
		ReflectionTestUtils.setField(bookGroup, "id", 234L);
		return bookGroup;
	}

	public static List<BookGroupResponse> mockBookGroupResponses() {
		return List.of(
			new BookGroupResponse(
				999L, "모임999", "모임 999에용", 5, true, false,
				2L, 100L,

				new BookGroupResponse.BookResponse(4452L, "http://bookImageUrl1.com"),
				new BookGroupResponse.OwnerResponse(1341234L,
					"http://ownerProfile1.com", "나는모임장이다1")
			),

			new BookGroupResponse(
				997L, "모임997", "모임 997에용", 5, true, false,
				5L, 2L,
				new BookGroupResponse.BookResponse(2083L, "http://bookImageUrl3.com"),
				new BookGroupResponse.OwnerResponse(941234L, "http://ownerProfile1.com",
					"나는모임장이다1")
			),

			new BookGroupResponse(
				995L, "모임995", "모임 995에용", 5, true, true,
				5L, 0L,
				new BookGroupResponse.BookResponse(442L, "http://bookImageUrl1.com"),
				new BookGroupResponse.OwnerResponse(1334L, "http://ownerProfile1.com", "나는모임장이다1")
			)
			,
			new BookGroupResponse(
				994L, "모임994", "모임 994에용", 5, false, true,
				3L, 30L,

				new BookGroupResponse.BookResponse(44L, "http://bookImageUrl1.com"),
				new BookGroupResponse.OwnerResponse(1341234L, "http://ownerProfile1.com", "나는모임장이다1")
			)
			,
			new BookGroupResponse(
				993L, "모임993", "모임 993에용", 5, false, false,
				4L, 4000L,

				new BookGroupResponse.BookResponse(913452L, "http://bookImageUrl1.com"),
				new BookGroupResponse.OwnerResponse(123234L, "http://ownerProfile1.com", "나는모임장이다1")
			)
		);
	}

}
