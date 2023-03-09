package com.dadok.gaerval.domain.book.api;

import static org.springframework.http.MediaType.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.BookIdResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponse;
import com.dadok.gaerval.domain.book.dto.response.BookResponses;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;
import com.dadok.gaerval.domain.book.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/books")
@RestController
@RequiredArgsConstructor
@Slf4j
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
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER','ROLE_ANONYMOUS')")
	public ResponseEntity<BookResponses> findBooksByQuery(@RequestParam(name = "query") String query) {
		log.info("[BookController]-[findBooksByQuery]-query : {}", query);

		return ResponseEntity.ok().body(bookService.findAllByKeyword(query));
	}

	/**
	 * <pre>
	 *     도서 ID를 통해 상세정보를 가져온다.
	 * </pre>
	 *
	 * @param bookId 검색어
	 * @return status : ok
	 */
	@GetMapping(value = "/{bookId}", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER','ROLE_ANONYMOUS')")
	public ResponseEntity<BookResponse> findBookDetail(@PathVariable(name = "bookId") Long bookId) {
		log.info("[BookController]-[BookResponse]-bookId : {}", bookId);
		return ResponseEntity.ok().body(bookService.findDetailById(bookId));
	}

	/**
	 * <pre>
	 *     도서 저장 요청을 통해 도서를 저장한다.
	 * </pre>
	 *
	 * @param bookCreateRequest 책 정보
	 * @return status : ok
	 */
	@PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ResponseEntity<BookIdResponse> saveBookDetail(@Valid @RequestBody BookCreateRequest bookCreateRequest) {
		log.info("[BookController]-[Void]-bookCreateRequest : {}", bookCreateRequest);
		Long bookId = bookService.createBookAndReturnId(bookCreateRequest);
		String redirectUri =
			ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + bookId.toString();
		return ResponseEntity.created(URI.create(redirectUri)).body(new BookIdResponse(bookId));
	}

	@GetMapping("/suggestions")
	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER','ROLE_ANONYMOUS')")
	public ResponseEntity<SuggestionsBookFindResponses> findSuggestionsBook(
		@ModelAttribute @Valid SuggestionsBookFindRequest request
	) {
		return ResponseEntity.ok(bookService.findSuggestionBooks(request));
	}

}
