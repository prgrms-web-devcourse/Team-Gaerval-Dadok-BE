package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.SummaryBookshelfResponse;

public interface BookshelfSupport {

	Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId);

	Optional<SummaryBookshelfResponse> findSummaryById(Long userId);

}
