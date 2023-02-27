package com.dadok.gaerval.domain.bookshelf.dto.response;

import java.util.List;

public record PopularBookshelvesOfJobResponses(String japGroup, int size,
											   List<SummaryBookshelfResponse> bookshelfResponses) {
}
