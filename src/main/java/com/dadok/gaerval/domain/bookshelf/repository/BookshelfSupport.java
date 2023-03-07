package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;
import java.util.Optional;

import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfDetailResponse;
import com.dadok.gaerval.domain.bookshelf.dto.response.BookShelfSummaryResponse;
import com.dadok.gaerval.domain.job.entity.JobGroup;

public interface BookshelfSupport {

	Optional<BookShelfDetailResponse> findByIdWithUserAndJob(Long userId);

	Optional<BookShelfSummaryResponse> findSummaryById(Long userId);

	List<BookShelfSummaryResponse> findSuggestionsByJobGroup(JobGroup jobGroup, Long userId, int limit);

	List<BookShelfSummaryResponse> findAllSuggestions(int limit);

	Optional<BookShelfDetailResponse> findBookShelfById(Long bookshelfId);
}
