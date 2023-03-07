package com.dadok.gaerval.domain.book_group.service;

import java.util.Optional;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public interface BookGroupService {

	BookGroupResponses findAllBookGroups(BookGroupSearchRequest request);

	BookGroupResponses findAllBookGroupsByUser(BookGroupSearchRequest request, Long userId);

	Long createBookGroup(Long userId, BookGroupCreateRequest request);

	BookGroupDetailResponse findGroup(Long userId, Long groupId);

	Optional<BookGroup> findById(Long groupId);

	void deleteBookGroup(Long groupId, Long userId);
}
