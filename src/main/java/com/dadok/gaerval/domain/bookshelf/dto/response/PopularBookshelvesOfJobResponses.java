package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record PopularBookshelvesOfJobResponses(String jobGroup,
											   List<BookShelfSummaryResponse> bookshelfResponses) {
}
