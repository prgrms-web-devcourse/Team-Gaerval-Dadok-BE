package com.dadok.gaerval.domain.book_group.repository;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupDetailResponse;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;

interface BookGroupSupport {

	BookGroupResponses findAllBy(BookGroupSearchRequest request);

	BookGroupDetailResponse findBookGroup(Long requestUserId, Long groupId);

}
