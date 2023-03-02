package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;

public interface BookshelfSupport {

	Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId);

}
