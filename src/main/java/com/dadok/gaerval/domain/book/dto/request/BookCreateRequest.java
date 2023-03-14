package com.dadok.gaerval.domain.book.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record BookCreateRequest(

	@NotNull
	@NotBlank(message = "title 입력되지 않았습니다.")
	@Size(max = 500, message = "title은 500자를 초과할 수 없습니다.")
	String title,

	@NotBlank(message = "author 입력되지 않았습니다.")
	String author,

	@NotBlank(message = "isbn 입력되지 않았습니다.")
	@Size(min = 10, max = 20, message = "isbn은 10자이상 20이하여야합니다.")
	String isbn,

	@NotBlank(message = "contents 입력되지 않았습니다.")
	@Size(max = 2000, message = "contents는 2000자를 초과할 수 없습니다.")
	String contents,

	@Size(max = 2083, message = "url은 2083자를 초과할 수 없습니다.")
	String url,

	@NotBlank(message = "imageUrl 입력되지 않았습니다.")
	@Size(max = 2083, message = "imageUrl 2083자를 초과할 수 없습니다.")
	String imageUrl,

	@NotBlank(message = "publisher 입력되지 않았습니다.")
	@Size(max = 20, message = "publisher 20자를 초과할 수 없습니다.")
	String publisher,

	@NotBlank(message = "apiProvider 입력되지 않았습니다.")
	@Size(max = 20, message = "apiProvider 20자를 초과할 수 없습니다.")
	String apiProvider
) {
}
