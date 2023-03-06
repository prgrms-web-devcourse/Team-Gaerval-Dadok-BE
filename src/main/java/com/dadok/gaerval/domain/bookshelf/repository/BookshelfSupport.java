package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;

public interface BookshelfSupport {

	Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId);

	Optional<BookShelfSummaryResponse> findSummaryById(Long userId);

}
