package com.dadok.gaerval.domain.book.api;

import static org.springframework.http.MediaType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.service.BookService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/books")
@RestController
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;

	/**
	 * <pre>
	 *     검색어를 입력받아 도서 목록을 외부 API로부터 받아온다.
	 * </pre>
	 *
	 * @param query 검색어
	 * @return status : ok
	 */
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookResponses> findBooksByQuery(@RequestParam(name = "query") String query) {
		return ResponseEntity.ok().body(bookService.findAllByKeyword(query));
	}
}
